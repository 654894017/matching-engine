package com.damon.matching.api.cmd;

import com.damon.cqrs.domain.Command;

public class CallAuctionCmd extends Command {
    public CallAuctionCmd(Long commandId, Long aggregateId) {
        super(commandId, aggregateId);
    }
}
