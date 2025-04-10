package com.damon.matching.api.cmd;

import com.damon.cqrs.domain.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockBuyCmd extends Command {
    private Long stockId;
    private Long price;
    private Long orderId;
    private Integer quantity;

    public StockBuyCmd(Long commandId, Long stockId) {
        super(commandId, stockId);
        this.stockId = stockId;
    }

}
