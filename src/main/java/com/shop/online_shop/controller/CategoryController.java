
package com.shop.online_shop.controller;

import com.shop.online_shop.entity.Category;
import com.shop.online_shop.repository.CategoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 1. Lấy danh sách danh mục
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @PathVariable Long id) {

        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Thêm danh mục
    @PostMapping
    public ResponseEntity<?> addCategory(
            @RequestBody Category category) {

        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Tên danh mục không được để trống");
        }

        category.setName(category.getName().trim());

        Category savedCategory =
                categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCategory);
    }

    // 4. Cập nhật danh mục
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {

        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Tên danh mục không được để trống");
        }

        return categoryRepository.findById(id)
                .map(existingCategory -> {

                    existingCategory.setName(
                            category.getName().trim()
                    );

                    Category updatedCategory =
                            categoryRepository.save(existingCategory);

                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long id) {

        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        categoryRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
