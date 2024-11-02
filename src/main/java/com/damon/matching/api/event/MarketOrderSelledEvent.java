package com.damon.matching.api.event;

import com.damon.cqrs.domain.Event;
import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class MarketOrderSelledEvent extends Event {
    private Long stockId;
    private Long orderId;
    private Integer totalQuantity;
    private LinkedHashSet<TradeOrder> tradeOrders;
    /**
     * 0 最优5档成交剩余撤销 1 最优5档成交剩余转限价单
     */
    private int entrustmentType;

    public MarketOrderSelledEvent() {
    }

    public MarketOrderSelledEvent(Long orderId, LinkedHashSet<TradeOrder> tradeOrders, Long stockId, Integer totalQuantity, int entrustmentType) {
        this.orderId = orderId;
        this.tradeOrders = tradeOrders;
        this.stockId = stockId;
        this.totalQuantity = totalQuantity;
        this.entrustmentType = entrustmentType;
    }

    public Integer undoneQuantity() {
        return totalQuantity - tradeOrders.stream().mapToInt(TradeOrder::getQuantity).sum();
    }

    public boolean isTransferLimitOrderEntrustment() {
        return entrustmentType == 1;
    }

    public boolean isDone() {
        return undoneQuantity() == 0;
    }

    public boolean isUndone() {
        return !isDone();
    }

    @Data
    public static class TradeOrder {
        private Long buyerOrderId;
        private Integer quantity;
        private Long price;
        private boolean isDone;

        public TradeOrder(Long buyerOrderId, boolean isDone, Integer quantity, Long price) {
            this.isDone = isDone;
            this.quantity = quantity;
            this.price = price;
            this.buyerOrderId = buyerOrderId;
        }

        public TradeOrder() {
        }
    }

}
