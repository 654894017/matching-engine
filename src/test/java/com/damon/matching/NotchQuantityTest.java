package com.damon.matching;

import cn.hutool.core.util.IdUtil;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.StockBuyCmd;
import com.damon.matching.api.cmd.StockGetCmd;
import com.damon.matching.api.cmd.StockSellCmd;
import com.damon.matching.api.dto.StockDTO;
import com.damon.matching.domain.StockCommandService;

public class NotchQuantityTest {
    public static void main(String[] args) throws InterruptedException {
        CqrsConfig cqrsConfig = MatchingConfig.init();
        IStockCommandService stockCommandService = new StockCommandService(cqrsConfig);
        StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd.setQuantity(1000);
        orderSellCmd.setPrice(99L);
        stockCommandService.sell(orderSellCmd);

        StockSellCmd orderSellCmd2 = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
        orderSellCmd2.setOrderId(IdUtil.getSnowflakeNextId());
        orderSellCmd2.setQuantity(1000);
        orderSellCmd2.setPrice(100L);
        stockCommandService.sell(orderSellCmd2);

        StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd.setQuantity(1000);
        buyOrderCmd.setPrice(98L);
        stockCommandService.buy(buyOrderCmd);

        StockBuyCmd buyOrderCmd2 = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
        buyOrderCmd2.setOrderId(IdUtil.getSnowflakeNextId());
        buyOrderCmd2.setQuantity(1000);
        buyOrderCmd2.setPrice(99L);
        stockCommandService.buy(buyOrderCmd2);

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
