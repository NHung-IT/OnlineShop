const API_URL = "/api/products";

const tableBody = document.getElementById("productTableBody");
const productModal = document.getElementById("productModal");
const productForm = document.getElementById("productForm");

let editingProductId = null;

// ==============================
// 1. HIỂN THỊ DANH SÁCH SẢN PHẨM
// ==============================

async function loadProducts() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Không thể tải danh sách sản phẩm");
        }

        const products = await response.json();
        displayProducts(products);

    } catch (error) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="error-message">
                    ${error.message}
                </td>
            </tr>
        `;
    }
}

function displayProducts(products) {
    tableBody.innerHTML = "";

    if (products.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6">Không có sản phẩm nào</td>
            </tr>
        `;
        return;
    }

    products.forEach(product => {
        const row = document.createElement("tr");

        const values = [
            product.id,
            product.name,
            Number(product.price).toLocaleString("vi-VN") + " đ",
            product.quantity,
            product.categoryId
        ];

        values.forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value ?? "";
            row.appendChild(cell);
        });

        const actionCell = document.createElement("td");

        actionCell.innerHTML = `
            <div class="action-buttons">
                <button class="edit-btn">Sửa</button>
                <button class="delete-btn">Xóa</button>
            </div>
        `;

        actionCell.querySelector(".edit-btn")
            .addEventListener("click", () => editProduct(product));

        actionCell.querySelector(".delete-btn")
            .addEventListener("click", () => deleteProduct(product.id));

        row.appendChild(actionCell);
        tableBody.appendChild(row);
    });
}

// ==============================
// 2. TÌM KIẾM SẢN PHẨM
// ==============================

async function searchProducts(keyword) {
    try {
        if (!keyword.trim()) {
            loadProducts();
            return;
        }

        const response = await fetch(
            `${API_URL}/search?keyword=${encodeURIComponent(keyword)}`
        );

        if (!response.ok) {
            throw new Error("Lỗi tìm kiếm sản phẩm");
        }

        const products = await response.json();
        displayProducts(products);

    } catch (error) {
        alert(error.message);
    }
}

// ==============================
// 3. LỌC SẢN PHẨM THEO GIÁ
// ==============================

async function filterProducts() {
    const minPrice = document.getElementById("minPrice").value;
    const maxPrice = document.getElementById("maxPrice").value;

    if (minPrice === "" || maxPrice === "") {
        alert("Vui lòng nhập cả giá thấp nhất và giá cao nhất");
        return;
    }

    if (
        !Number.isFinite(Number(minPrice)) ||
        !Number.isFinite(Number(maxPrice)) ||
        Number(minPrice) < 0 ||
        Number(maxPrice) < 0 ||
        Number(minPrice) > Number(maxPrice)
    ) {
        alert("Khoảng giá không hợp lệ");
        return;
    }

    try {
        const params = new URLSearchParams({
            minPrice,
            maxPrice
        });

        const response = await fetch(`${API_URL}/filter?${params}`);

        if (!response.ok) {
            throw new Error("Lỗi lọc sản phẩm");
        }

        const products = await response.json();
        displayProducts(products);

    } catch (error) {
        alert(error.message);
    }
}

// ==============================
// 4. MỞ VÀ ĐÓNG MODAL
// ==============================

function openModal() {
    productModal.classList.remove("hidden");
}

function closeModal() {
    productModal.classList.add("hidden");
    productForm.reset();
    editingProductId = null;
}

// Mở modal để thêm sản phẩm
function openAddModal() {
    productForm.reset();

    editingProductId = null;

    document.getElementById("modalTitle").textContent =
        "Thêm sản phẩm";

    document.getElementById("saveProductBtn").textContent =
        "Thêm sản phẩm";

    openModal();
}

// Mở modal để sửa sản phẩm
function editProduct(product) {
    editingProductId = product.id;

    document.getElementById("modalTitle").textContent =
        "Cập nhật sản phẩm";

    document.getElementById("saveProductBtn").textContent =
        "Cập nhật";

    document.getElementById("productName").value =
        product.name ?? "";

    document.getElementById("productDescription").value =
        product.description ?? "";

    document.getElementById("productPrice").value =
        product.price ?? "";

    document.getElementById("productQuantity").value =
        product.quantity ?? "";

    document.getElementById("productImageUrl").value =
        product.imageUrl ?? "";

    document.getElementById("productCategoryId").value =
        product.categoryId ?? "";

    openModal();
}

// ==============================
// 5. LẤY VÀ KIỂM TRA DỮ LIỆU FORM
// ==============================

function getProductFormData() {
    const name = document.getElementById("productName").value.trim();
    const description = document.getElementById("productDescription").value.trim();
    const price = document.getElementById("productPrice").value;
    const quantity = document.getElementById("productQuantity").value;
    const imageUrl = document.getElementById("productImageUrl").value.trim();
    const categoryId = document.getElementById("productCategoryId").value;

    if (!name) {
        throw new Error("Tên sản phẩm không được để trống");
    }

    if (
        price === "" ||
        !Number.isFinite(Number(price)) ||
        Number(price) < 0
    ) {
        throw new Error("Giá sản phẩm không hợp lệ");
    }

    if (
        quantity === "" ||
        !Number.isInteger(Number(quantity)) ||
        Number(quantity) < 0
    ) {
        throw new Error("Số lượng phải là số nguyên không âm");
    }

    if (
        categoryId === "" ||
        !Number.isSafeInteger(Number(categoryId)) ||
        Number(categoryId) <= 0
    ) {
        throw new Error("ID danh mục không hợp lệ");
    }

    return {
        name,
        description,
        price: Number(price),
        quantity: Number(quantity),
        imageUrl,
        categoryId: Number(categoryId)
    };
}

// ==============================
// 6. THÊM / CẬP NHẬT SẢN PHẨM
// ==============================

async function saveProduct(event) {
    event.preventDefault();

    try {
        const productData = getProductFormData();

        const isEditing = editingProductId !== null;

        const url = isEditing
            ? `${API_URL}/${editingProductId}`
            : API_URL;

        const method = isEditing ? "PUT" : "POST";

        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(productData)
        });

        if (!response.ok) {
            const errorMessage = await response.text();

            throw new Error(
                errorMessage || "Không thể lưu sản phẩm"
            );
        }

        alert(
            isEditing
                ? "Cập nhật sản phẩm thành công"
                : "Thêm sản phẩm thành công"
        );

        closeModal();
        loadProducts();

    } catch (error) {
        alert(error.message);
    }
}

// ==============================
// 7. XÓA SẢN PHẨM
// ==============================

async function deleteProduct(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Không thể xóa sản phẩm");
        }

        alert("Xóa sản phẩm thành công");
        loadProducts();

    } catch (error) {
        alert(error.message);
    }
}

// ==============================
// 8. GẮN CÁC SỰ KIỆN
// ==============================

// Tìm kiếm
document.getElementById("searchInput")
    .addEventListener("input", event => {
        searchProducts(event.target.value);
    });

// Lọc
document.getElementById("filterBtn")
    .addEventListener("click", filterProducts);

// Làm mới
document.getElementById("resetBtn")
    .addEventListener("click", () => {
        document.getElementById("searchInput").value = "";
        document.getElementById("minPrice").value = "";
        document.getElementById("maxPrice").value = "";

        loadProducts();
    });

// Mở modal thêm sản phẩm
document.getElementById("addProductBtn")
    .addEventListener("click", openAddModal);

// Đóng modal
document.getElementById("closeModalBtn")
    .addEventListener("click", closeModal);

document.getElementById("cancelModalBtn")
    .addEventListener("click", closeModal);

// Lưu sản phẩm
productForm.addEventListener("submit", saveProduct);

// Đóng modal khi nhấn ra ngoài
productModal.addEventListener("click", event => {
    if (event.target === productModal) {
        closeModal();
    }
});

// Đóng modal bằng phím Escape
document.addEventListener("keydown", event => {
    if (event.key === "Escape") {
        closeModal();
    }
});

// ==============================
// 9. KHỞI ĐỘNG
// ==============================

loadProducts();