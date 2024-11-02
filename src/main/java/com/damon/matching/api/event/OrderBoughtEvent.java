package com.damon.matching.api.event;

import com.damon.cqrs.domain.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderBoughtEvent extends Event {
    private Long stockId;
    private Long entrustPrice;
    private Long buyPrice;
    private Long orderId;
    private Integer originalQuantity;
    private Integer tradingQuantity;
    private Boolean isDone;

    public OrderBoughtEvent(Long stockId, Long entrustPrice, Long buyPrice, Long orderId, Integer originalQuantity, Integer tradingQuantity, Boolean isDone) {
        this.stockId = stockId;
        this.entrustPrice = entrustPrice;
        this.buyPrice = buyPrice;
        this.orderId = orderId;
        this.originalQuantity = originalQuantity;
        this.tradingQuantity = tradingQuantity;
        this.isDone = isDone;
    }

    public OrderBoughtEvent() {
    }

    public Boolean isDone() {
        return isDone;
    }
}
