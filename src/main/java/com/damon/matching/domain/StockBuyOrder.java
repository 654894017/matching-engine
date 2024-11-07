package com.damon.matching.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class StockBuyOrder {

    private Long price;

    private Long createTime;

    private Integer originalQuantity;

    private Integer quantity;

    private Long orderId;

    public StockBuyOrder(Long orderId, Long price, Integer quantity, Long createTime) {
        this.price = price;
        this.createTime = createTime;
        this.quantity = quantity;
        this.orderId = orderId;
        this.originalQuantity = quantity;
    }

    public StockBuyOrder(Long orderId) {
        this.orderId = orderId;
    }

    public StockBuyOrder() {
    }

    public int subtract(Integer quantity) {
        if (this.quantity < quantity) {
            return -1;
        }
        this.quantity = this.quantity - quantity;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockBuyOrder buyOrder = (StockBuyOrder) o;
        return Objects.equals(orderId, buyOrder.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }
}
