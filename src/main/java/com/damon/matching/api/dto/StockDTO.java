package com.damon.matching.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;
import java.util.TreeMap;

@Getter
@Setter
@ToString
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
    /**
     * 价格档位降序
     */
    private TreeMap<Long, Long> buyerPriceNotchQuantityMap = new TreeMap<>(Comparator.reverseOrder());
    /**
     * 价格档位升序
     */
    private TreeMap<Long, Long> sellerPriceNotchQuantityMap = new TreeMap<>();


}
