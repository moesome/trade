package com.moesome.trade.common.config;

public class QueueConfig {
    // 队列
    public static final String STOCK_DECREMENT_QUEUE = "stockDecrementQueue";
    public static final String COIN_DECREMENT_QUEUE = "coinDecrementQueue";
    public static final String ORDER_FAILED_QUEUE = "orderFailedQueue";
    public static final String STOCK_ROLLBACK_QUEUE = "stockRollbackQueue";
    public static final String ORDER_SUCCEED_QUEUE = "orderSucceedQueue";
    public static final String EMAIL_QUEUE = "emailQueue";
    // 路由键
    public static final String STOCK_DECREMENT_QUEUE_KEY = "stockDecrement";
    public static final String COIN_DECREMENT_QUEUE_KEY = "coinDecrement";
    public static final String ORDER_FAILED_QUEUE_KEY = "orderFailed";
    public static final String STOCK_ROLLBACK_QUEUE_KEY = "stockRollback";
    public static final String ORDER_SUCCEED_QUEUE_KEY = "orderSucceed";
    public static final String EMAIL_QUEUE_KEY = "email";
    // 交换器
    public static final String ORDER_DIRECT_EXCHANGE = "orderTopicExchange";
}
