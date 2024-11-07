package com.damon.matching.domain;

import com.damon.cqrs.command.CommandService;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.*;
import com.damon.matching.api.dto.StockDTO;


public class StockCommandService extends CommandService<Stock> implements IStockCommandService {
    public StockCommandService(CqrsConfig cqrsConfig) {
        super(cqrsConfig);
    }

    @Override
    public int callAuction(CallAuctionCmd cmd) {
        return super.process(cmd, stock -> stock.callAuction(cmd)).join();
    }

    @Override
    public int match(StockOrderMatchCmd cmd) {
        return super.process(cmd, stock -> stock.match(cmd)).join();
    }

    @Override
    public int buy(StockBuyCmd cmd) {
        return super.process(cmd, stock -> stock.buy(cmd)).join();
    }

    @Override
    public int buy(StockMarketBuyCmd cmd) {
        return super.process(cmd, stock -> stock.buy(cmd)).join();
    }

    @Override
    public int sell(StockMarketSellCmd cmd) {
        return super.process(cmd, stock -> stock.sell(cmd)).join();
    }

    @Override
    public int sell(StockSellCmd cmd) {
        return super.process(cmd, stock -> stock.sell(cmd)).join();
    }

    @Override
    public int cancel(StockOrderCancelCmd cmd) {
        return super.process(cmd, stock -> stock.cancel(cmd)).join();
    }

    @Override
    public StockDTO get(StockGetCmd cmd) {
        return super.process(cmd, stock -> {
            StockDTO stockDTO = new StockDTO();
            stockDTO.setId(stock.getId());
            stockDTO.setRealtimePrice(stock.getRealtimePrice());
            stockDTO.setNotchPrice(stock.getNotchPrice());
            // 获取5档
            stockDTO.setBuyerPriceNotchQuantityMap(stock.calculateBuyerNotchQuantity(5));
            stockDTO.setSellerPriceNotchQuantityMap(stock.calculateSellerNotchQuantity(5));
            return stockDTO;
        }).join();
    }

    @Override
    public Stock getAggregateSnapshot(long aggregateId, Class<Stock> classes) {
        Stock stock = new Stock(aggregateId);
        // 这边可以查询数据库获取上一个交易日的收盘价作为初始价格
        stock.setRealtimePrice(999L);
        // 一个档位的价格,根据上一个交易日的涨停价除以100档位(按照涨停10个点计算)
        stock.setNotchPrice(1L);
        return stock;
    }
}
