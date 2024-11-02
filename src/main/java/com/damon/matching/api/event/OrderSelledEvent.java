package com.damon.matching.api.event;

import com.damon.cqrs.domain.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSelledEvent extends Event {
    private Long price;
    private Long orderId;
    private Integer originalQuantity;
    private Integer tradingQuantity;
    private Boolean isDone;

    public OrderSelledEvent(Long price, Long orderId, Integer originalQuantity, Integer tradingQuantity, Boolean isDone) {
        this.price = price;
        this.orderId = orderId;
        this.originalQuantity = originalQuantity;
        this.tradingQuantity = tradingQuantity;
        this.isDone = isDone;
    }

    public OrderSelledEvent() {
    }

    public Boolean isDone() {
        return isDone;
    }
}
