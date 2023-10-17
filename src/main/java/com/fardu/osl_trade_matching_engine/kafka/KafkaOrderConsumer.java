package com.fardu.osl_trade_matching_engine.kafka;

import com.fardu.osl_trade_matching_engine.engine.OrderBookManager;
import com.fardu.osl_trade_matching_engine.engine.OrderMatchingEngine;
import com.fardu.osl_trade_matching_engine.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderConsumer {

    private final OrderBookManager orderBookManager;

    @Autowired
    public KafkaOrderConsumer(OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    @KafkaListener(topics = "orders", groupId = "order_group", containerFactory = "orderKafkaListenerContainerFactory")
    public void consume(Order order) {
        orderBookManager.sortIncomingOrders(order);
    }
}