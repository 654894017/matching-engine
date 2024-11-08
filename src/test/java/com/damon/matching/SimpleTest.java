package com.damon.matching;

import cn.hutool.core.util.IdUtil;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.*;
import com.damon.matching.domain.StockCommandService;

public class SimpleTest {
    public static void main(String[] args) throws InterruptedException {
        CqrsConfig cqrsConfig = MatchingConfig.init();
        IStockCommandService stockCommandService = new StockCommandService(cqrsConfig);
        for (int i = 0; i < 1; i++) {
            StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
            buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
            buyOrderCmd.setQuantity(1000);
            buyOrderCmd.setPrice(101L);
            stockCommandService.buy(buyOrderCmd);
        }
        for (int i = 0; i < 1; i++) {
            StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
            orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
            orderSellCmd.setQuantity(1000);
            orderSellCmd.setPrice(99L);
            stockCommandService.sell(orderSellCmd);
        }

        StockMarketBuyCmd stockMarketBuyCmd = new StockMarketBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        stockMarketBuyCmd.setQuantity(500);
        stockMarketBuyCmd.setOrderId(IdUtil.getSnowflakeNextId());
        stockMarketBuyCmd.setEntrustmentType(1);
        stockCommandService.buy(stockMarketBuyCmd);

        StockMarketSellCmd stockMarketSellCmd = new StockMarketSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        stockMarketSellCmd.setQuantity(500);
        stockMarketSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
        stockMarketSellCmd.setEntrustmentType(0);
        stockCommandService.sell(stockMarketSellCmd);

        int result = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10000L)).join();
        System.out.println(stockCommandService.get(new StockGetCmd(IdUtil.getSnowflakeNextId(), 10000L)).getRealtimePrice());
    }
}
