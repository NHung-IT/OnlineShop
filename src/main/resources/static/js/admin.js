const API_URL = "/api/products";

const tableBody = document.getElementById("productTableBody");

// Lấy danh sách sản phẩm
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

// Hiển thị danh sách sản phẩm
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

        actionCell.querySelector(".edit-btn").addEventListener("click", () => {
            editProduct(product);
        });

        actionCell.querySelector(".delete-btn").addEventListener("click", () => {
            deleteProduct(product.id);
        });

        row.appendChild(actionCell);
        tableBody.appendChild(row);
    });
}

// Tìm kiếm sản phẩm
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

// Lọc sản phẩm theo giá
async function filterProducts() {
    const minPrice = document.getElementById("minPrice").value;
    const maxPrice = document.getElementById("maxPrice").value;

    if (minPrice === "" || maxPrice === "") {
        alert("Vui lòng nhập cả giá thấp nhất và giá cao nhất");
        return;
    }

    if (Number(minPrice) > Number(maxPrice)) {
        alert("Giá thấp nhất không được lớn hơn giá cao nhất");
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

// Xóa sản phẩm
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

// Chỉnh sửa sản phẩm
async function editProduct(product) {
    const name = prompt("Tên sản phẩm:", product.name);
    if (name === null) return;

    const price = prompt("Giá sản phẩm:", product.price);
    if (price === null) return;

    const quantity = prompt("Số lượng:", product.quantity);
    if (quantity === null) return;

    const categoryId = prompt("ID danh mục:", product.categoryId);
    if (categoryId === null) return;

    const updatedProduct = {
        ...product,
        name: name.trim(),
        price: Number(price),
        quantity: Number(quantity),
        categoryId: Number(categoryId)
    };

    if (
        !updatedProduct.name ||
        !Number.isFinite(updatedProduct.price) ||
        updatedProduct.price < 0 ||
        !Number.isInteger(updatedProduct.quantity) ||
        updatedProduct.quantity < 0 ||
        !Number.isInteger(updatedProduct.categoryId) ||
        updatedProduct.categoryId <= 0
    ) {
        alert("Thông tin sản phẩm không hợp lệ");
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${product.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedProduct)
        });

        if (!response.ok) {
            throw new Error("Không thể cập nhật sản phẩm");
        }

        alert("Cập nhật sản phẩm thành công");
        loadProducts();

    } catch (error) {
        alert(error.message);
    }
}

// Sự kiện tìm kiếm
document.getElementById("searchInput").addEventListener("input", event => {
    searchProducts(event.target.value);
});

// Sự kiện lọc
document.getElementById("filterBtn").addEventListener("click", filterProducts);

// Sự kiện làm mới
document.getElementById("resetBtn").addEventListener("click", () => {
    document.getElementById("searchInput").value = "";
    document.getElementById("minPrice").value = "";
    document.getElementById("maxPrice").value = "";

    loadProducts();
});

// Tạm thời nút thêm sản phẩm sẽ được triển khai ở bước tiếp theo
document.getElementById("addProductBtn").addEventListener("click", () => {
    alert("Chức năng thêm sản phẩm sẽ được hoàn thiện ở bước tiếp theo.");
});

// Tải dữ liệu khi mở trang
loadProducts();