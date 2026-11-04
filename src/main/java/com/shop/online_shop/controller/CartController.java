
package com.shop.online_shop.controller;

import com.shop.online_shop.dto.CartItemRequest;
import com.shop.online_shop.dto.CartResponse;
import com.shop.online_shop.entity.CartItem;
import com.shop.online_shop.entity.User;
import com.shop.online_shop.repository.UserRepository;
import com.shop.online_shop.service.CartService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(
            CartService cartService,
            UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // Kiểm tra user trong JWT có đúng với userId trên URL không
    private boolean isOwner(Long userId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng!"));

        return user.getId().equals(userId);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequest request,
            Authentication authentication) {

        try {
            if (!isOwner(userId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Bạn không có quyền truy cập giỏ hàng này!"));
            }

            CartItem cartItem = cartService.addToCart(
                    userId,
                    request.getProductId(),
                    request.getQuantity()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Thêm sản phẩm vào giỏ hàng thành công!",
                    "productId", cartItem.getProduct().getId(),
                    "productName", cartItem.getProduct().getName(),
                    "quantity", cartItem.getQuantity()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(
            @PathVariable Long userId,
            Authentication authentication) {

        try {
            if (!isOwner(userId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Bạn không có quyền truy cập giỏ hàng này!"));
            }

            CartResponse cart = cartService.getCart(userId);
            return ResponseEntity.ok(cart);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestBody CartItemRequest request,
            Authentication authentication) {

        try {
            if (!isOwner(userId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Bạn không có quyền truy cập giỏ hàng này!"));
            }

            CartItem cartItem = cartService.updateCartItem(
                    userId,
                    productId,
                    request.getQuantity()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật giỏ hàng thành công!",
                    "productId", cartItem.getProduct().getId(),
                    "productName", cartItem.getProduct().getName(),
                    "quantity", cartItem.getQuantity()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId,
            Authentication authentication) {

        try {
            if (!isOwner(userId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Bạn không có quyền truy cập giỏ hàng này!"));
            }

            cartService.removeFromCart(userId, productId);

            return ResponseEntity.ok(Map.of(
                    "message", "Xóa sản phẩm khỏi giỏ hàng thành công!"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
