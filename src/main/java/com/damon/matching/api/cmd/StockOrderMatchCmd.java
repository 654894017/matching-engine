package com.damon.matching.api.cmd;

import com.damon.cqrs.domain.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockOrderMatchCmd extends Command {

    public StockOrderMatchCmd(Long commandId, Long stockId) {
        super(commandId, stockId);
    }
}
