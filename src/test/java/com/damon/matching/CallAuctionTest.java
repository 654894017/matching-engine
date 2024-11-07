package com.damon.matching;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.*;
import com.damon.matching.api.dto.StockDTO;
import com.damon.matching.domain.StockCommandService;
import org.junit.Test;

/**
 * 集合竞价测试
 *
 * https://m.gelonghui.com/p/513097
 *
 */
public class CallAuctionTest {
    @Test
    public void call_auction_test() {
        CqrsConfig cqrsConfig = MatchingConfig.init();
        IStockCommandService stockCommandService = new StockCommandService(cqrsConfig);
        StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd.setQuantity(13);
        buyOrderCmd.setPrice(1002L);
        stockCommandService.buy(buyOrderCmd);
        StockBuyCmd buyOrderCmd2 = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd2.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd2.setQuantity(12);
        buyOrderCmd2.setPrice(1001L);
        stockCommandService.buy(buyOrderCmd2);
        StockBuyCmd buyOrderCmd3 = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd3.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd3.setQuantity(18);
        buyOrderCmd3.setPrice(1000L);
        stockCommandService.buy(buyOrderCmd3);
        StockBuyCmd buyOrderCmd4 = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd4.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd4.setQuantity(15);
        buyOrderCmd4.setPrice(999L);
        stockCommandService.buy(buyOrderCmd4);
        StockBuyCmd buyOrderCmd5 = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd5.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd5.setQuantity(20);
        buyOrderCmd5.setPrice(998L);
        stockCommandService.buy(buyOrderCmd5);


        StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd.setQuantity(15);
        orderSellCmd.setPrice(1002L);
        stockCommandService.sell(orderSellCmd);
        StockSellCmd orderSellCmd2 = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd2.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd2.setQuantity(13);
        orderSellCmd2.setPrice(1001L);
        stockCommandService.sell(orderSellCmd2);
        StockSellCmd orderSellCmd3 = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd3.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd3.setQuantity(20);
        orderSellCmd3.setPrice(1000L);
        stockCommandService.sell(orderSellCmd3);
        StockSellCmd orderSellCmd4 = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd4.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd4.setQuantity(18);
        orderSellCmd4.setPrice(999l);
        stockCommandService.sell(orderSellCmd4);
        StockSellCmd orderSellCmd5 = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd5.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd5.setQuantity(10);
        orderSellCmd5.setPrice(998L);
        stockCommandService.sell(orderSellCmd5);
        stockCommandService.callAuction(new CallAuctionCmd(IdUtil.getSnowflakeNextId(), 10000L));
        Long price = stockCommandService.get(new StockGetCmd(IdUtil.getSnowflakeNextId(), 10000L)).getRealtimePrice();
        System.out.println("集合竞价 : "  + price);
        Assert.equals(1000L, price);
        display5Notch(stockCommandService);
        System.out.println("--------------------------");
        for (int i = 0; i < 100; i++) {
            stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10000L));
        }
        display5Notch(stockCommandService);
    }

    public void display5Notch(IStockCommandService stockCommandService) {
        StockDTO stock = stockCommandService.get(new StockGetCmd(IdUtil.getSnowflakeNextId(), 10000L));
        System.out.println("买单5个档位:");
        stock.getBuyerPriceNotchQuantityMap().forEach((price, quantity) -> {
            System.out.println(price + ":" + quantity);
        });
        System.out.println("实时价格:" + stock.getRealtimePrice());
        System.out.println("卖单5个档位:");
        stock.getSellerPriceNotchQuantityMap().forEach((price, quantity) -> {
            System.out.println(price + ":" + quantity);
        });
    }
}
