package com.fardu.osl_trade_matching_engine.engine;


import com.fardu.osl_trade_matching_engine.kafka.KafkaOrderProducer;
import com.fardu.osl_trade_matching_engine.models.Order;
import com.fardu.osl_trade_matching_engine.models.OrderBook;
import com.fardu.osl_trade_matching_engine.persistence.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderMatchingEngine {

    private final KafkaOrderProducer kafkaOrderProducer;
    private final TradeService tradeService;

    @Autowired
    public OrderMatchingEngine(KafkaOrderProducer kafkaOrderProducer, TradeService tradeService) {
        this.kafkaOrderProducer = kafkaOrderProducer;
        this.tradeService = tradeService;
    }

    public void processOrder(Order order, OrderBook orderBook) {

        Optional<Order> matchedOrder = orderBook.matchOrder(order);
        if (matchedOrder.isPresent()) {
            // Execute the trade
            executeTrade(order, matchedOrder.get(), orderBook);
        } else {
            // Add the order to the order book
            orderBook.addOrder(order);
            kafkaOrderProducer.sendMessage(order.getInstrument() + " trading: " + " Order added to book: " + order);
        }
    }

    private void executeTrade(Order order1, Order order2, OrderBook orderBook) {
        int tradeQuantity = Math.min(order1.getQuantity(), order2.getQuantity());

        // Adjust the quantities of the orders
        order1.setQuantity(order1.getQuantity() - tradeQuantity);
        order2.setQuantity(order2.getQuantity() - tradeQuantity);

        // If there is any remaining quantity, add the orders back to the order book
        if (order1.getQuantity() > 0) {
            orderBook.addOrder(order1);
        }
        if (order2.getQuantity() > 0) {
            orderBook.addOrder(order2);
        }

        tradeService.createAndSaveTrade(order1.getInstrument(), tradeQuantity, order1.getPrice());
        kafkaOrderProducer.sendMessage(order1.getInstrument() + " trading: " +"Trade executed: " + tradeQuantity + " @ " + order1.getPrice());
    }
}