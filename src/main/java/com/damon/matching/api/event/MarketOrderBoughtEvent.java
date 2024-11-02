package com.damon.matching.api.event;

import com.damon.cqrs.domain.Event;
import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class MarketOrderBoughtEvent extends Event {
    private Long stockId;
    private Long orderId;
    private Integer totalQuantity;
    private LinkedHashSet<TradeOrder> tradeOrders;
    /**
     * 1 最优5档成交剩余撤销 0 最优5档成交剩余转限价单
     */
    private int entrustmentType;

    public MarketOrderBoughtEvent() {
    }

    public MarketOrderBoughtEvent(Long orderId, LinkedHashSet<TradeOrder> tradeOrders, Long stockId, Integer totalQuantity, int entrustmentType) {
        this.orderId = orderId;
        this.tradeOrders = tradeOrders;
        this.stockId = stockId;
        this.totalQuantity = totalQuantity;
        this.entrustmentType = entrustmentType;
    }

    public boolean isCancelEntrustment() {
        return entrustmentType == 0;
    }

    public boolean isTransferLimitOrderEntrustment() {
        return entrustmentType == 1;
    }

    public Integer undoneQuantity() {
        return totalQuantity - tradeOrders.stream().mapToInt(TradeOrder::getQuantity).sum();
    }

    public boolean isDone() {
        return undoneQuantity() == 0;
    }

    public boolean isUndone() {
        return !isDone();
    }

    @Data
    public static class TradeOrder {
        private Long sellerOrderId;
        private Integer quantity;
        private Long price;
        private boolean isDone;

        public TradeOrder(Long sellerOrderId, boolean isDone, Integer quantity, Long price) {
            this.isDone = isDone;
            this.quantity = quantity;
            this.price = price;
            this.sellerOrderId = sellerOrderId;
        }

        public TradeOrder() {
        }
    }

}
