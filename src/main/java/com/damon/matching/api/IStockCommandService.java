package com.damon.matching.api;


import com.damon.matching.api.cmd.*;
import com.damon.matching.api.dto.StockDTO;

public interface IStockCommandService {
    int callAuction(CallAuctionCmd cmd);

    int match(StockOrderMatchCmd cmd);

    int buy(StockBuyCmd cmd);

    int cancel(StockOrderCancelCmd cmd);

    int buy(StockMarketBuyCmd cmd);

    int sell(StockMarketSellCmd cmd);

    int sell(StockSellCmd cmd);

    StockDTO get(StockGetCmd cmd);
}
