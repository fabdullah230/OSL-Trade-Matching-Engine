package com.fardu.osl_trade_matching_engine.engine;


import com.fardu.osl_trade_matching_engine.models.Order;
import com.fardu.osl_trade_matching_engine.models.OrderBook;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class OrderBookManager {

    private final OrderBook btcOrderBook = new OrderBook();
    private final OrderBook ethOrderBook = new OrderBook();
    private final OrderBook usdtOrderBook = new OrderBook();

    private final OrderMatchingEngine orderMatchingEngine;


    public Map<String, TreeMap<Double, Queue<Order>>> fullOrderBookSnapshot(String symbol){
        if (symbol.equals("BTC")){
            return btcOrderBook.getFullOrderBook();
        } else if (symbol.equals("ETH")) {
            return ethOrderBook.getFullOrderBook();
        } else if (symbol.equals("USDT")) {
            return usdtOrderBook.getFullOrderBook();
        } else {
            throw new RuntimeException();
        }
    }

    public String cancelExistingOrder(String symbol, String side, String id){
        if (symbol.equals("BTC")){
            return btcOrderBook.cancelOrderById(id, side) ?  "Canceled order " + id + " for symbol BTC" :  "Could not find order with id " + id + " to cancel";
        } else if (symbol.equals("ETH")) {
            return ethOrderBook.cancelOrderById(id, side) ?  "Canceled order " + id + " for symbol ETH" :  "Could not find order with id " + id + " to cancel";
        } else if (symbol.equals("USDT")) {
            return usdtOrderBook.cancelOrderById(id, side) ?  "Canceled order " + id + " for symbol USDT" :  "Could not find order with id " + id + " to cancel";
        } else {
            throw new RuntimeException();
        }
    }

    public void sortIncomingOrders(Order order){
        if (order.getInstrument().equals("BTC")){
            orderMatchingEngine.processOrder(order, btcOrderBook);
        } else if (order.getInstrument().equals("ETH")) {
            orderMatchingEngine.processOrder(order, ethOrderBook);
        } else if (order.getInstrument().equals("USDT")) {
            orderMatchingEngine.processOrder(order, usdtOrderBook );
        }
    }

}
