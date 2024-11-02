package com.damon.matching.api.dto;

import com.damon.matching.domain.StockBuyOrder;
import com.damon.matching.domain.StockSellOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class StockDTO {
    private Long id;
    /**
     * 股票实时价格
     */
    private Long realtimePrice;
    /**
     * 一个档位的价格
     */
    private Long notchPrice;

    private Map<Long, Boolean> tradeMap = new HashMap<>();
    /**
     * 先按价格档位降序, 在同档位内按下单时间升序, 类型: <price,<orderId, order>>
     */
    private TreeMap<Long, TreeMap<Long, StockBuyOrder>> buyOrderMap = new TreeMap<>(Comparator.reverseOrder());
    /**
     * 先按价格档位升序, 在同档位内按下单时间升序, 类型: <price,<orderId, order>>
     */
    private TreeMap<Long, TreeMap<Long, StockSellOrder>> sellOrderMap = new TreeMap<>();


}
