package com.damon.matching;

import cn.hutool.core.util.IdUtil;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.matching.api.IStockCommandService;
import com.damon.matching.api.cmd.StockBuyCmd;
import com.damon.matching.api.cmd.StockOrderMatchCmd;
import com.damon.matching.api.cmd.StockSellCmd;
import com.damon.matching.domain.StockCommandService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceTest {
    public static void main(String[] args) throws InterruptedException {
        CqrsConfig cqrsConfig = MatchingConfig.init();
        IStockCommandService stockCommandService = new StockCommandService(cqrsConfig);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch1 = new CountDownLatch(400 * 500 * 10);
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10000L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10001L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10002L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10003L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockBuyCmd buyOrderCmd = new StockBuyCmd(IdUtil.getSnowflakeNextId(), 10004L);
                    buyOrderCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    buyOrderCmd.setQuantity(1000);
                    buyOrderCmd.setPrice(100L);
                    stockCommandService.buy(buyOrderCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10000L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10001L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10002L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10003L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                    latch1.countDown();
                }
            });
        }
        for (int k = 0; k < 400; k++) {
            executorService.submit(() -> {
                for (int i = 0; i < 500; i++) {
                    StockSellCmd orderSellCmd = new StockSellCmd(IdUtil.getSnowflakeNextId(), 10004L);
                    orderSellCmd.setOrderId(IdUtil.getSnowflakeNextId());
                    orderSellCmd.setQuantity(1000);
                    orderSellCmd.setPrice(100L);
                    stockCommandService.sell(orderSellCmd);
                    latch1.countDown();
                }
            });
        }
        latch1.await();
        System.out.println("start -----------");
        int count = 400 * 500 * 5;
        CountDownLatch latch = new CountDownLatch(count);
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        service.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                List<CompletableFuture<Integer>> futureList = new ArrayList<>();
                for (int j = 0; j < 512; j++) {
                    CompletableFuture<Integer> future = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10000L));
                    futureList.add(future);
                }
                futureList.forEach(f -> {
                    int result = f.join();
                    if (result == 0) {
                        latch.countDown();
                    }
                });
            }
        });

        service.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                List<CompletableFuture<Integer>> futureList = new ArrayList<>();
                for (int j = 0; j < 512; j++) {
                    CompletableFuture<Integer> future = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10001L));
                    futureList.add(future);
                }
                futureList.forEach(f -> {
                    int result = f.join();
                    if (result == 0) {
                        latch.countDown();
                    }
                });
            }
        });

        service.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                List<CompletableFuture<Integer>> futureList = new ArrayList<>();
                for (int j = 0; j < 512; j++) {
                    CompletableFuture<Integer> future = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10002L));
                    futureList.add(future);
                }
                futureList.forEach(f -> {
                    int result = f.join();
                    if (result == 0) {
                        latch.countDown();
                    }
                });
            }
        });

        service.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                List<CompletableFuture<Integer>> futureList = new ArrayList<>();
                for (int j = 0; j < 512; j++) {
                    CompletableFuture<Integer> future = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10003L));
                    futureList.add(future);
                }
                futureList.forEach(f -> {
                    int result = f.join();
                    if (result == 0) {
                        latch.countDown();
                    }
                });
            }
        });

        service.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                List<CompletableFuture<Integer>> futureList = new ArrayList<>();
                for (int j = 0; j < 512; j++) {
                    CompletableFuture<Integer> future = stockCommandService.match(new StockOrderMatchCmd(IdUtil.getSnowflakeNextId(), 10004L));
                    futureList.add(future);
                }
                futureList.forEach(f -> {
                    int result = f.join();
                    if (result == 0) {
                        latch.countDown();
                    }
                });
            }
        });
        latch.await();
        int costTime = (int) ((System.currentTimeMillis() - start) / 1000);
        System.out.println(count / costTime + "/tps");
    }
}
