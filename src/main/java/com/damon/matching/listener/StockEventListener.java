package com.damon.matching.listener;

import com.alibaba.fastjson.JSONObject;
import com.damon.cqrs.domain.Event;
import com.damon.cqrs.kafka.KafkaEventOrderlyListener;
import com.damon.cqrs.kafka.config.KafkaConsumerConfig;

import java.util.List;

/**
 * @author xianpinglu
 */
public class StockEventListener extends KafkaEventOrderlyListener {
    public StockEventListener(KafkaConsumerConfig config) {
        super(config);
    }

    @Override
    public void process(List<List<Event>> list) {
        list.forEach(events -> events.forEach(event -> {
            System.out.println(JSONObject.toJSONString(event));
        }));
    }
}
