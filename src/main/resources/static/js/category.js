const CATEGORY_API_URL = "/api/categories";

const categoryTableBody = document.getElementById("categoryTableBody");
const categoryModal = document.getElementById("categoryModal");
const categoryForm = document.getElementById("categoryForm");

let categories = [];
let editingCategoryId = null;

// 1. Lấy danh sách danh mục
async function loadCategories() {
    try {
        const response = await fetch(CATEGORY_API_URL);

        if (!response.ok) {
            throw new Error("Không thể tải danh sách danh mục");
        }

        categories = await response.json();

        displayCategories(categories);

    } catch (error) {
        console.error(error);

        categoryTableBody.innerHTML = `
            <tr>
                <td colspan="3" class="error-message">
                    ${error.message}
                </td>
            </tr>
        `;
    }
}

// 2. Hiển thị danh sách
function displayCategories(categoryList) {
    categoryTableBody.innerHTML = "";

    if (categoryList.length === 0) {
        categoryTableBody.innerHTML = `
            <tr>
                <td colspan="3">Chưa có danh mục nào</td>
            </tr>
        `;
        return;
    }

    categoryList.forEach(category => {
        const row = document.createElement("tr");

        const idCell = document.createElement("td");
        idCell.textContent = category.id;

        const nameCell = document.createElement("td");
        nameCell.textContent = category.name;

        const actionCell = document.createElement("td");

        const actionContainer = document.createElement("div");
        actionContainer.className = "action-buttons";

        const editButton = document.createElement("button");
        editButton.className = "edit-btn";
        editButton.textContent = "Sửa";
        editButton.addEventListener("click", () => {
            editCategory(category.id);
        });

        const deleteButton = document.createElement("button");
        deleteButton.className = "delete-btn";
        deleteButton.textContent = "Xóa";
        deleteButton.addEventListener("click", () => {
            deleteCategory(category.id);
        });

        actionContainer.appendChild(editButton);
        actionContainer.appendChild(deleteButton);

        actionCell.appendChild(actionContainer);

        row.appendChild(idCell);
        row.appendChild(nameCell);
        row.appendChild(actionCell);

        categoryTableBody.appendChild(row);
    });
}

// 3. Tìm kiếm danh mục
function searchCategories(keyword) {
    const normalizedKeyword = keyword.trim().toLowerCase();

    const result = categories.filter(category =>
        category.name.toLowerCase().includes(normalizedKeyword)
    );

    displayCategories(result);
}

// 4. Mở modal
function openCategoryModal() {
    categoryModal.classList.remove("hidden");
}

// 5. Đóng modal
function closeCategoryModal() {
    categoryModal.classList.add("hidden");
    categoryForm.reset();
    editingCategoryId = null;
}

// 6. Mở modal thêm danh mục
function openAddCategoryModal() {
    editingCategoryId = null;

    categoryForm.reset();

    document.getElementById("categoryModalTitle").textContent =
        "Thêm danh mục";

    document.getElementById("saveCategoryBtn").textContent =
        "Lưu danh mục";

    openCategoryModal();
}

// 7. Mở modal sửa danh mục
function editCategory(id) {
    const category = categories.find(item => item.id === id);

    if (!category) {
        alert("Không tìm thấy danh mục");
        return;
    }

    editingCategoryId = id;

    document.getElementById("categoryName").value = category.name;

    document.getElementById("categoryModalTitle").textContent =
        "Cập nhật danh mục";

    document.getElementById("saveCategoryBtn").textContent =
        "Cập nhật";

    openCategoryModal();
}

// 8. Lưu danh mục
async function saveCategory(event) {
    event.preventDefault();

    const name = document
        .getElementById("categoryName")
        .value
        .trim();

    if (!name) {
        alert("Tên danh mục không được để trống");
        return;
    }

    const categoryData = { name };

    const isEditing = editingCategoryId !== null;

    const url = isEditing
        ? `${CATEGORY_API_URL}/${editingCategoryId}`
        : CATEGORY_API_URL;

    const method = isEditing ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(categoryData)
        });

        if (!response.ok) {
            const message = await response.text();
            throw new Error(message || "Không thể lưu danh mục");
        }

        alert(
            isEditing
                ? "Cập nhật danh mục thành công"
                : "Thêm danh mục thành công"
        );

        closeCategoryModal();
        await loadCategories();

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// 9. Xóa danh mục
async function deleteCategory(id) {
    const confirmed = confirm(
        "Bạn có chắc chắn muốn xóa danh mục này?"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${CATEGORY_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const message = await response.text();

            throw new Error(
                message || "Không thể xóa danh mục. Có thể danh mục đang được sử dụng."
            );
        }

        alert("Xóa danh mục thành công");

        await loadCategories();

    } catch (error) {
        console.error(error);

        alert(error.message);
    }
}

// 10. Các sự kiện
document
    .getElementById("addCategoryBtn")
    .addEventListener("click", openAddCategoryModal);

document
    .getElementById("closeCategoryModalBtn")
    .addEventListener("click", closeCategoryModal);

document
    .getElementById("cancelCategoryBtn")
    .addEventListener("click", closeCategoryModal);

categoryForm.addEventListener("submit", saveCategory);

document
    .getElementById("searchCategoryInput")
    .addEventListener("input", event => {
        searchCategories(event.target.value);
    });

categoryModal.addEventListener("click", event => {
    if (event.target === categoryModal) {
        closeCategoryModal();
    }
});

document.addEventListener("keydown", event => {
    if (event.key === "Escape") {
        closeCategoryModal();
    }
});

// Khởi chạy
loadCategories();