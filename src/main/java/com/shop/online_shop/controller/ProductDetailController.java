
package com.shop.online_shop.controller;

import com.shop.online_shop.entity.Product;
import com.shop.online_shop.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductDetailController {

    private final ProductService productService;

    public ProductDetailController(ProductService productService) {
        this.productService = productService;
    }

    // Trang chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public String productDetail(
            @PathVariable Long id,
            Model model) {

        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);

        return "user/product-detail";
    }
}
