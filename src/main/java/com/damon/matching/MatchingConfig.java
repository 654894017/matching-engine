package com.damon.matching;

import com.damon.cqrs.cache.DefaultAggregateCaffeineCache;
import com.damon.cqrs.cache.IAggregateCache;
import com.damon.cqrs.config.AggregateSlotLock;
import com.damon.cqrs.config.CqrsConfig;
import com.damon.cqrs.event.DefaultEventSendingShceduler;
import com.damon.cqrs.event.EventCommittingService;
import com.damon.cqrs.event.ISendMessageService;
import com.damon.cqrs.event_store.DataSourceMapping;
import com.damon.cqrs.event_store.DefaultEventShardingRouting;
import com.damon.cqrs.event_store.MysqlEventOffset;
import com.damon.cqrs.event_store.MysqlEventStore;
import com.damon.cqrs.kafka.KafkaSendService;
import com.damon.cqrs.kafka.config.KafkaConsumerConfig;
import com.damon.cqrs.recovery.AggregateRecoveryService;
import com.damon.cqrs.snapshot.DefaultAggregateSnapshootService;
import com.damon.cqrs.snapshot.IAggregateSnapshootService;
import com.damon.cqrs.store.IEventOffset;
import com.damon.cqrs.store.IEventStore;
import com.damon.matching.listener.StockEventListener;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;

public class MatchingConfig {
    private static String bootstrapServers = "";

    public static HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3307/cqrs?serverTimezone=UTC&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(20);
        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getTypeName());
        return dataSource;
    }

    public static CqrsConfig init() {
        List<DataSourceMapping> list = Lists.newArrayList(
                DataSourceMapping.builder().dataSourceName("ds0").dataSource(dataSource()).tableNumber(1).build()
        );
        DefaultEventShardingRouting route = new DefaultEventShardingRouting();
        IEventStore store = new MysqlEventStore(list, 8, route);
        IEventOffset offset = new MysqlEventOffset(list);
        IAggregateSnapshootService aggregateSnapshootService = new DefaultAggregateSnapshootService(1, 6);
        IAggregateCache aggregateCache = new DefaultAggregateCaffeineCache(1024 * 1024, 60);

        //如果event走cdc模式,不用初始化
        //initEventListener(store, offset);

        AggregateSlotLock aggregateSlotLock = new AggregateSlotLock(4096);
        AggregateRecoveryService aggregateRecoveryService = new AggregateRecoveryService(store, aggregateCache, aggregateSlotLock);
        EventCommittingService eventCommittingService = new EventCommittingService(
                store, 8, 1024 * 4, 32, aggregateRecoveryService
        );

        CqrsConfig cqrsConfig = CqrsConfig.builder().
                eventStore(store).aggregateSnapshootService(aggregateSnapshootService).aggregateCache(aggregateCache).
                aggregateSlotLock(aggregateSlotLock).
                eventCommittingService(eventCommittingService).build();
        return cqrsConfig;
    }

    private static void initEventListener(IEventStore store, IEventOffset offset) {
        ISendMessageService sendingService = new KafkaSendService("stock_event_topic", bootstrapServers);
        new DefaultEventSendingShceduler(store, offset, sendingService, 5);
        KafkaConsumerConfig config = new KafkaConsumerConfig();
        config.setTopic("stock_event_topic");
        config.setGroupId("stock_matching_group_id");
        config.setBootstrapServers(bootstrapServers);
        new StockEventListener(config);
    }


}