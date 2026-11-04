package com.shop.online_shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private List<OrderItemResponse> items;

    public OrderResponse(
            Long orderId,
            Long userId,
            LocalDateTime orderDate,
            BigDecimal totalAmount,
            String status,
            String shippingAddress,
            List<OrderItemResponse> items) {

        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}