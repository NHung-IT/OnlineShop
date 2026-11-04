
package com.shop.online_shop.controller;

import com.shop.online_shop.entity.Product;
import com.shop.online_shop.service.ProductService;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Lấy danh sách sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Lấy chi tiết sản phẩm
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Tìm kiếm sản phẩm
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }

    // Lọc sản phẩm theo giá
    @GetMapping("/filter")
    public List<Product> filterProducts(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        return productService.filterProducts(minPrice, maxPrice);
    }

    // Thêm sản phẩm
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Xóa sản phẩm thành công";
    }
}
