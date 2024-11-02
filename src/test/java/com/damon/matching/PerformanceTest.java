package com.damon.matching;

import cn.hutool.core.util.IdUtil;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.StockBuyCmd;
import com.damon.matching.api.cmd.StockGetCmd;
import com.damon.matching.api.cmd.StockOrderMatchCmd;
import com.damon.matching.api.cmd.StockSellCmd;
import com.damon.matching.api.dto.StockDTO;
import com.damon.matching.domain.StockCommandService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceTest {
    public static void main(String[] args) throws InterruptedException {
        CqrsConfig cqrsConfig = MatchingConfig.init();
        IStockCommandService stockCommandService = new StockCommandService(cqrsConfig);
        ExecutorService service1 = Executors.newVirtualThreadPerTaskExecutor();
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10001L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10002L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10003L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                }
            });
        }

        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10001L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10002L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            service1.submit(() -> {
                for (int i = 0; i < 5000; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10003L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                }
            });
        }
        CountDownLatch latch = new CountDownLatch(200 * 10000);
        AtomicLong count = new AtomicLong();
        long start = System.currentTimeMillis();
        ExecutorService service3 = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 400; i++) {
            service3.submit(() -> {
                for (; ; ) {
                    int result = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10000L));
                    if (result != -1) {
                        latch.countDown();
                    } else {
                        count.addAndGet(1);
                        Thread.sleep(1);
                    }
                }
            });
        }
        for (int i = 0; i < 400; i++) {
            service3.submit(() -> {
                for (; ; ) {
                    int result = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10001L));
                    if (result != -1) {
                        latch.countDown();
                    } else {
                        count.addAndGet(1);
                        Thread.sleep(1);
                    }
                }
            });
        }
        for (int i = 0; i < 400; i++) {
            service3.submit(() -> {
                for (; ; ) {
                    int result = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10002L));
                    if (result != -1) {
                        latch.countDown();
                    } else {
                        count.addAndGet(1);
                        Thread.sleep(1);
                    }
                }
            });
        }
        for (int i = 0; i < 400; i++) {
            service3.submit(() -> {
                for (; ; ) {
                    int result = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10003L));
                    if (result != -1) {
                        latch.countDown();
                    } else {
                        count.addAndGet(1);
                        Thread.sleep(1);
                    }
                }
            });
        }
        latch.await();
        System.out.println(count.get());
        System.out.println(System.currentTimeMillis() - start);
        StockDTO stock = stockCommandService.get(new StockGetCmd(IdUtil.getSnowflakeNextId(), 10000L));
        System.out.println(stock);
        System.out.println(1);
    }
}
