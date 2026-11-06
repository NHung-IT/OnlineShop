
package com.shop.online_shop.controller;

import com.shop.online_shop.entity.Product;
import com.shop.online_shop.repository.CategoryRepository;
import com.shop.online_shop.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public HomeController(
            ProductService productService,
            CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long categoryId,
            Model model) {

        // Lấy tất cả danh mục từ database
        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        // Lấy sản phẩm theo danh mục nếu có lựa chọn
        List<Product> products;

        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        // Danh sách sản phẩm chính của Home
        model.addAttribute("products", products);

        // Tạo danh sách riêng cho Hero
        List<Product> heroProducts = new ArrayList<>(products);

        // Xáo trộn ngẫu nhiên
        Collections.shuffle(heroProducts);

        // Chỉ lấy tối đa 3 sản phẩm
        heroProducts = heroProducts.subList(
                0,
                Math.min(3, heroProducts.size())
        );

        model.addAttribute("heroProducts", heroProducts);

        model.addAttribute("selectedCategoryId", categoryId);

        return "user/index";
    }
}
