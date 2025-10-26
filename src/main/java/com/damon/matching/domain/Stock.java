package com.damon.matching.domain;

import com.damon.cqrs.domain.AggregateRoot;
import com.damon.matching.api.cmd.*;
import com.damon.matching.api.event.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Stock extends AggregateRoot {
    /**
     * 股票实时价格
     */
    private Long realtimePrice;
    /**
     * 一个档位的价格
     */
    private Long notchPrice;

    private Set<Long> tradedOrderIdSet = new HashSet<>();
    /**
     * 先按价格档位降序, 在同档位内按下单时间升序, 类型: <price,<orderId, order>>
     */
    private TreeMap<Long, TreeMap<Long, StockBuyOrder>> buyOrderMap = new TreeMap<>(Comparator.reverseOrder());
    /**
     * 先按价格档位升序, 在同档位内按下单时间升序, 类型: <price,<orderId, order>>
     */
    private TreeMap<Long, TreeMap<Long, StockSellOrder>> sellOrderMap = new TreeMap<>();

    public Stock(Long id) {
        super(id);
    }

    /**
     * 计算卖方N档价位列表
     *
     * @param notch
     * @return
     */
    public TreeMap<Long, Long> calculateSellerNotchQuantity(int notch) {
        int count = 0;
        TreeMap<Long, Long> result = new TreeMap<>();
        for (Map.Entry<Long, TreeMap<Long, StockSellOrder>> entry : sellOrderMap.entrySet()) {
            TreeMap<Long, StockSellOrder> sellOrders = entry.getValue();
            Long price = entry.getKey();
            if (sellOrders != null && !sellOrders.isEmpty()) {
                count++;
                Long totalNumber = sellOrders.values().stream().mapToLong(StockSellOrder::getQuantity).sum();
                result.put(price, totalNumber);
                if (count == notch) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 计算买方N档价位列表
     *
     * @param notch
     * @return
     */
    public TreeMap<Long, Long> calculateBuyerNotchQuantity(int notch) {
        int count = 0;
        TreeMap<Long, Long> result = new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<Long, TreeMap<Long, StockBuyOrder>> entry : buyOrderMap.entrySet()) {
            TreeMap<Long, StockBuyOrder> buyOrders = entry.getValue();
            Long price = entry.getKey();
            if (buyOrders != null && !buyOrders.isEmpty()) {
                count++;
                Long totalNumber = buyOrders.values().stream().mapToLong(StockBuyOrder::getQuantity).sum();
                result.put(price, totalNumber);
                if (count == notch) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 集合竞价(开盘价/收盘价都是通过此方法)
     * <p>
     * https://m.gelonghui.com/p/513097
     *
     * @return
     */
    public int callAuction(CallAuctionCmd cmd) {
        Map<Long, Long> sellPriceMap = new HashMap<>();
        //计算卖方每个档位的总的待交易量(小于等于档位的待交易量都统计)
        sellOrderMap.keySet().forEach(price -> {
            Long totalQuantity = sellOrderMap.tailMap(price).values().stream().flatMap(
                    treeMap -> treeMap.values().stream()
            ).mapToLong(StockSellOrder::getQuantity).sum();
            sellPriceMap.put(price, totalQuantity);
        });

        Map<Long, Long> buyPriceMap = new HashMap<>();
        //计算买方每个档位的总的待交易量(大于等于档位的待交易量都统计)
        buyOrderMap.keySet().forEach(price -> {
            Long totalQuantity = buyOrderMap.tailMap(price).values().stream().flatMap(
                    treeMap -> treeMap.values().stream()
            ).mapToLong(StockBuyOrder::getQuantity).sum();
            buyPriceMap.put(price, totalQuantity);
        });
        //每个档位最大可成交的交易量
        TreeMap<Long, Long> maxTradeMap = new TreeMap<>(Comparator.reverseOrder());
        sellPriceMap.forEach((price, sellTotalQuantity) -> {
            Long buyTotalQuantity = buyPriceMap.get(price);
            if (buyTotalQuantity != null) {
                Long maxTradeQuantity = Math.min(buyTotalQuantity, sellTotalQuantity);
                maxTradeMap.put(maxTradeQuantity, price);
            }
        });
        //取出最大可成交的交易量,作为集合竞价成功价格
        Map.Entry<Long, Long> entry = maxTradeMap.firstEntry();
        if (!maxTradeMap.isEmpty() && entry.getValue() != null) {
            applyNewEvent(new CallAuctionSucceedEvent(entry.getValue()));
        }
        return 0;
    }

    /**
     * 市价单购买
     *
     * @param cmd
     * @return
     */
    public int buy(StockMarketBuyCmd cmd) {
        Integer remainingQuantity = cmd.getQuantity();
        NavigableMap<Long, TreeMap<Long, StockSellOrder>> market5NotchMap = sellOrderMap.subMap(
                realtimePrice,
                true,
                realtimePrice + 5 * notchPrice,
                true
        );
        LinkedHashSet<MarketOrderBoughtEvent.TradeOrder> tradeOrders = new LinkedHashSet<>();
        MarketOrderBoughtEvent orderBoughtEvent = new MarketOrderBoughtEvent(
                cmd.getOrderId(),
                tradeOrders,
                cmd.getStockId(),
                cmd.getQuantity(),
                cmd.getEntrustmentType()
        );
        for (TreeMap<Long, StockSellOrder> sellOrders : market5NotchMap.values()) {
            for (StockSellOrder sellOrder : sellOrders.values()) {
                int sellQuantity = sellOrder.getQuantity();
                MarketOrderBoughtEvent.TradeOrder tradeOrder = new MarketOrderBoughtEvent.TradeOrder(
                        sellOrder.getOrderId(),
                        sellQuantity <= remainingQuantity,
                        Math.min(remainingQuantity, sellQuantity),
                        sellOrder.getPrice()
                );
                tradeOrders.add(tradeOrder);
                remainingQuantity -= tradeOrder.getQuantity();
                if (remainingQuantity <= 0) {
                    applyNewEvent(orderBoughtEvent);
                    return 0;
                }
            }
        }
        if (tradeOrders.isEmpty()) {
            return -1;
        }
        applyNewEvent(orderBoughtEvent);
        return 0;
    }

    /**
     * 市价单售卖
     *
     * @param cmd
     * @return
     */
    public int sell(StockMarketSellCmd cmd) {
        Integer remainingQuantity = cmd.getQuantity();
        NavigableMap<Long, TreeMap<Long, StockBuyOrder>> market5NotchMap = buyOrderMap.subMap(
                realtimePrice + 5 * notchPrice,
                true,
                realtimePrice,
                true
        );
        LinkedHashSet<MarketOrderSelledEvent.TradeOrder> tradeOrders = new LinkedHashSet<>();
        MarketOrderSelledEvent orderSelledEvent = new MarketOrderSelledEvent(
                cmd.getOrderId(),
                tradeOrders,
                cmd.getStockId(),
                cmd.getQuantity(),
                cmd.getEntrustmentType()
        );

        for (TreeMap<Long, StockBuyOrder> buyOrders : market5NotchMap.values()) {
            for (StockBuyOrder buyOrder : buyOrders.values()) {
                int buyQuantity = buyOrder.getQuantity();
                MarketOrderSelledEvent.TradeOrder tradeOrder = new MarketOrderSelledEvent.TradeOrder(
                        buyOrder.getOrderId(),
                        buyQuantity <= remainingQuantity,
                        Math.min(remainingQuantity, buyQuantity),
                        buyOrder.getPrice()
                );
                tradeOrders.add(tradeOrder);
                remainingQuantity -= tradeOrder.getQuantity();
                if (remainingQuantity <= 0) {
                    applyNewEvent(orderSelledEvent);
                    return 0;
                }
            }
        }
        if (tradeOrders.isEmpty()) {
            return -1;
        }
        applyNewEvent(orderSelledEvent);
        return 0;
    }

    /**
     * 限价卖单
     *
     * @param cmd
     * @return
     */
    public int sell(StockSellCmd cmd) {
        Boolean isTraded = tradedOrderIdSet.contains(cmd.getOrderId());
        if (isTraded) {
            return -1;
        } else {
            applyNewEvent(new OrderSelleEntrustSucceedEvent(
                    cmd.getOrderId(),
                    cmd.getPrice(),
                    System.nanoTime(),
                    cmd.getQuantity()
            ));
            return 0;
        }
    }

    /**
     * 取消委托
     *
     * @param cmd
     * @return
     */
    public int cancel(StockOrderCancelCmd cmd) {
        Boolean isTraded = tradedOrderIdSet.contains(cmd.getOrderId());
        if (isTraded) {
            return -1;
        } else {
            applyNewEvent(new OrderCancelledEvent(
                    cmd.getOrderId(),
                    cmd.isBuyOrder() ? 1 : 0,
                    cmd.getPrice()
            ));
            return 0;
        }
    }

    /**
     * 限价买单
     *
     * @param cmd
     * @return
     */
    public int buy(StockBuyCmd cmd) {
        Boolean isTraded = tradedOrderIdSet.contains(cmd.getOrderId());
        if (isTraded) {
            return -1;
        } else {
            applyNewEvent(new OrderBuyEntrustSucceedEvent(
                    cmd.getOrderId(),
                    cmd.getPrice(),
                    System.nanoTime(),
                    cmd.getQuantity())
            );
            return 0;

        }
    }

    /**
     * 撮合交易
     *
     * @param cmd
     * @return
     */
    public int match(StockOrderMatchCmd cmd) {
        if (buyOrderMap.isEmpty()) {
            return -1;
        }
        TreeMap<Long, StockBuyOrder> buyOrders = buyOrderMap.firstEntry().getValue();
        if (buyOrders.isEmpty()) {
            return -1;
        }
        Map.Entry<Long, StockBuyOrder> buyOrderEntry = buyOrders.firstEntry();
        if (buyOrderEntry == null) {
            return -1;
        }
        StockBuyOrder buyOrder = buyOrderEntry.getValue();
        if (buyOrder == null) {
            return -1;
        }
        if (sellOrderMap.isEmpty()) {
            return -1;
        }
        TreeMap<Long, StockSellOrder> sellOrders = sellOrderMap.firstEntry().getValue();
        if (sellOrders.isEmpty()) {
            return -1;
        }
        Map.Entry<Long, StockSellOrder> sellOrderEntry = sellOrders.firstEntry();
        if (sellOrderEntry == null) {
            return -1;
        }
        StockSellOrder sellOrder = sellOrderEntry.getValue();
        if (sellOrder == null) {
            return -1;
        }
        if (buyOrder.getPrice() < sellOrder.getPrice()) {
            return -1;
        }
        boolean isSellDone = sellOrder.getQuantity() <= buyOrder.getQuantity();
        boolean isBuyDone = sellOrder.getQuantity() >= buyOrder.getQuantity();
        applyNewEvent(new OrderSelledEvent(
                sellOrder.getPrice(),
                sellOrder.getOrderId(),
                sellOrder.getOriginalQuantity(),
                Math.min(buyOrder.getQuantity(), sellOrder.getQuantity()),
                isSellDone
        ));
        applyNewEvent(new OrderBoughtEvent(
                getId(),
                buyOrder.getPrice(),
                sellOrder.getPrice(),
                buyOrder.getOrderId(),
                sellOrder.getOriginalQuantity(),
                Math.min(buyOrder.getQuantity(), sellOrder.getQuantity()),
                isBuyDone
        ));
        return 0;
    }

    private void apply(MarketOrderBoughtEvent event) {
        event.getTradeOrders().forEach(tradeOrder -> {
            TreeMap<Long, StockSellOrder> priceSellOrders = sellOrderMap.get(tradeOrder.getPrice());
            if (tradeOrder.isDone()) {
                priceSellOrders.remove(tradeOrder.getSellerOrderId());
            } else {
                StockSellOrder sellOrder = priceSellOrders.get(tradeOrder.getSellerOrderId());
                sellOrder.subtract(tradeOrder.getQuantity());
            }
            if (priceSellOrders.isEmpty()) {
                sellOrderMap.remove(tradeOrder.getPrice());
            }
        });
        if (event.isUndone() && event.isTransferLimitOrderEntrustment()) {
            TreeMap<Long, StockBuyOrder> stockBuyOrders = buyOrderMap.computeIfAbsent(
                    realtimePrice, price -> new TreeMap<>()
            );
            StockBuyOrder buyOrder = new StockBuyOrder(
                    event.getOrderId(),
                    realtimePrice,
                    event.undoneQuantity(),
                    System.nanoTime()
            );
            stockBuyOrders.put(event.getOrderId(), buyOrder);
            tradedOrderIdSet.add(event.getOrderId());
        }

        if (!event.getTradeOrders().isEmpty()) {
            MarketOrderBoughtEvent.TradeOrder tradeOrder = event.getTradeOrders().getLast();
            this.realtimePrice = tradeOrder.getPrice();
        }
    }

    private void apply(MarketOrderSelledEvent event) {
        event.getTradeOrders().forEach(tradeOrder -> {
            TreeMap<Long, StockBuyOrder> priceSellOrders = buyOrderMap.get(tradeOrder.getPrice());
            if (tradeOrder.isDone()) {
                priceSellOrders.remove(tradeOrder.getBuyerOrderId());
            } else {
                StockBuyOrder buyOrder = priceSellOrders.get(tradeOrder.getBuyerOrderId());
                buyOrder.subtract(tradeOrder.getQuantity());
            }
            if (priceSellOrders.isEmpty()) {
                buyOrderMap.remove(tradeOrder.getPrice());
            }
        });

        if (event.isUndone() && event.isTransferLimitOrderEntrustment()) {
            TreeMap<Long, StockSellOrder> stockSellOrders = sellOrderMap.computeIfAbsent(
                    realtimePrice, price -> new TreeMap<>()
            );
            StockSellOrder sellOrder = new StockSellOrder(
                    event.getOrderId(),
                    realtimePrice,
                    event.undoneQuantity(),
                    System.nanoTime()
            );
            stockSellOrders.put(event.getOrderId(), sellOrder);
            tradedOrderIdSet.add(event.getOrderId());
        }

        if (!event.getTradeOrders().isEmpty()) {
            MarketOrderSelledEvent.TradeOrder tradeOrder = event.getTradeOrders().getLast();
            this.realtimePrice = tradeOrder.getPrice();
        }
    }

    private void apply(OrderCancelledEvent event) {
        if (event.isBuyOrder()) {
            TreeMap<Long, StockBuyOrder> buyOrders = buyOrderMap.get(event.getPrice());
            buyOrders.remove(event.getOrderId());
        } else {
            TreeMap<Long, StockSellOrder> sellOrders = sellOrderMap.get(event.getPrice());
            sellOrders.remove(event.getOrderId());
        }
    }

    private void apply(OrderSelledEvent event) {
        TreeMap<Long, StockSellOrder> sellOrders = sellOrderMap.get(event.getPrice());
        if (event.isDone()) {
            sellOrders.remove(event.getOrderId());
        } else {
            StockSellOrder sellOrder = sellOrders.get(event.getOrderId());
            sellOrder.subtract(event.getTradingQuantity());
        }
        if (sellOrders.isEmpty()) {
            sellOrderMap.remove(event.getPrice());
        }
        this.realtimePrice = event.getPrice();
    }

    private void apply(OrderBoughtEvent event) {
        TreeMap<Long, StockBuyOrder> buyOrders = buyOrderMap.get(event.getEntrustPrice());
        if (event.isDone()) {
            buyOrders.remove(event.getOrderId());
        } else {
            StockBuyOrder buyOrder = buyOrders.get(event.getOrderId());
            buyOrder.subtract(event.getTradingQuantity());
        }
        if (buyOrders.isEmpty()) {
            buyOrderMap.remove(event.getEntrustPrice());
        }
        this.realtimePrice = event.getBuyPrice();
    }

    private void apply(OrderBuyEntrustSucceedEvent event) {
        TreeMap<Long, StockBuyOrder> stockBuyOrders = buyOrderMap.computeIfAbsent(
                event.getPrice(), price -> new TreeMap<>()
        );
        StockBuyOrder buyOrder = new StockBuyOrder(
                event.getOrderId(),
                event.getPrice(),
                event.getQuantity(),
                event.getCreateTime()
        );
        stockBuyOrders.put(buyOrder.getOrderId(), buyOrder);
        tradedOrderIdSet.add(event.getOrderId());
    }

    private void apply(OrderSelleEntrustSucceedEvent event) {
        TreeMap<Long, StockSellOrder> stockSellOrders = sellOrderMap.computeIfAbsent(
                event.getPrice(), price -> new TreeMap<>()
        );
        StockSellOrder sellOrder = new StockSellOrder(
                event.getOrderId(),
                event.getPrice(),
                event.getQuantity(),
                event.getCreateTime()
        );
        stockSellOrders.put(sellOrder.getOrderId(), sellOrder);
        tradedOrderIdSet.add(event.getOrderId());
    }

    private void apply(CallAuctionSucceedEvent event) {
        this.realtimePrice = event.getPrice();
    }
}
