package com.shop.online_shop.service;

import com.shop.online_shop.dto.OrderItemResponse;
import com.shop.online_shop.dto.OrderResponse;
import com.shop.online_shop.entity.*;
import com.shop.online_shop.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse checkout(Long userId, String shippingAddress) {

        // 1. Kiểm tra địa chỉ giao hàng
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new RuntimeException("Địa chỉ giao hàng không được để trống!");
        }

        // 2. Tìm người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng!"));

        // 3. Tìm giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy giỏ hàng!"));

        // 4. Kiểm tra giỏ hàng rỗng
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống!");
        }

        // 5. Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItemResponse> responseItems = new ArrayList<>();

        // 6. Duyệt từng sản phẩm trong giỏ
        for (CartItem cartItem : new ArrayList<>(cart.getItems())) {

            Product product = productRepository.findById(
                    cartItem.getProduct().getId()
            ).orElseThrow(() -> new RuntimeException(
                    "Không tìm thấy sản phẩm!"));

            Integer quantity = cartItem.getQuantity();

            // Kiểm tra tồn kho
            if (product.getQuantity() == null ||
                    product.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Sản phẩm " + product.getName()
                                + " không đủ số lượng trong kho!");
            }

            // Tính thành tiền
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(subtotal);

            // Tạo chi tiết đơn hàng
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(product.getPrice());

            order.getItems().add(orderItem);

            // Trừ tồn kho
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            
            // Chuẩn bị dữ liệu trả về
            responseItems.add(new OrderItemResponse(
                    product.getId(),
                    product.getName(),
                    quantity,
                    product.getPrice(),
                    subtotal
            ));
        }

        // 7. Lưu tổng tiền
        order.setTotalAmount(totalAmount);

        // 8. Lưu đơn hàng và chi tiết đơn hàng
        Order savedOrder = orderRepository.save(order);

        // 9. Xóa sản phẩm trong giỏ hàng
        cart.getItems().clear();

        // 10. Trả về kết quả
        return new OrderResponse(
                savedOrder.getId(),
                user.getId(),
                savedOrder.getOrderDate(),
                totalAmount,
                savedOrder.getStatus(),
                savedOrder.getShippingAddress(),
                responseItems
        );
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng!"
                ));

        List<Order> orders = orderRepository
                .findByUserIdOrderByOrderDateDesc(userId);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItemResponse> itemResponses = new ArrayList<>();

            for (OrderItem item : order.getItems()) {

                BigDecimal subtotal = item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                itemResponses.add(new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        subtotal
                ));
            }

            responses.add(new OrderResponse(
                    order.getId(),
                    order.getUser().getId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getShippingAddress(),
                    itemResponses
            ));
        }

        return responses;
    }

}