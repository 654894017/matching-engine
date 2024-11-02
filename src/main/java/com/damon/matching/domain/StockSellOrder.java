package com.damon.matching.domain;

import lombok.Data;

@Data
public class StockSellOrder {
    private Long price;
    private Long createTime;
    private Integer quantity;
    private Long orderId;
    private Integer originalQuantity;

    public StockSellOrder(Long orderId, Long price, Integer quantity, Long createTime) {
        this.price = price;
        this.createTime = createTime;
        this.quantity = quantity;
        this.orderId = orderId;
        this.originalQuantity = quantity;

    }

    public StockSellOrder(Long orderId) {
        this.orderId = orderId;
    }

    public StockSellOrder() {
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

        StockSellOrder sellOrder = (StockSellOrder) o;
        return orderId.equals(sellOrder.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}
