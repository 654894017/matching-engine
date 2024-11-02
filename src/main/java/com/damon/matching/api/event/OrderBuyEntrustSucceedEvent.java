package com.damon.matching.api.event;

import com.damon.cqrs.domain.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderBuyEntrustSucceedEvent extends Event {
    private Long price;

    private Long createTime;

    private Integer quantity;

    private Long orderId;

    public OrderBuyEntrustSucceedEvent(Long orderId, Long price, Long createTime, Integer quantity) {
        this.createTime = createTime;
        this.quantity = quantity;
        this.orderId = orderId;
        this.price = price;
    }

    public OrderBuyEntrustSucceedEvent() {
    }

}
