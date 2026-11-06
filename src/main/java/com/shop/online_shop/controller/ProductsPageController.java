package com.shop.online_shop.controller;

import com.shop.online_shop.entity.Product;
import com.shop.online_shop.repository.CategoryRepository;
import com.shop.online_shop.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductsPageController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductsPageController(
            ProductService productService,
            CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            Model model) {

        // Lấy danh sách danh mục cho sidebar
        model.addAttribute("categories", categoryRepository.findAll());

        // Lấy sản phẩm theo điều kiện tìm kiếm và lọc
        List<Product> products = new ArrayList<>(
                productService.searchAndFilter(
                        keyword,
                        categoryId,
                        minPrice,
                        maxPrice
                )
        );

        // Sắp xếp theo giá
        if ("price-asc".equals(sort)) {
            products.sort(Comparator.comparing(Product::getPrice));
        } else if ("price-desc".equals(sort)) {
            products.sort(
                    Comparator.comparing(Product::getPrice).reversed()
            );
        }

        model.addAttribute("products", products);

        // Thông tin danh mục đang chọn
        String selectedCategoryName = null;

        if (categoryId != null) {
            selectedCategoryName = categoryRepository.findById(categoryId)
                    .map(category -> category.getName())
                    .orElse("Danh mục sản phẩm");
        }

        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        // Giữ lại điều kiện tìm kiếm và sắp xếp
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);

        return "user/products";
    }
}