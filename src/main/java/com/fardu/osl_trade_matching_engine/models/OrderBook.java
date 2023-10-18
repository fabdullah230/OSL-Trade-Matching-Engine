package com.fardu.osl_trade_matching_engine.models;


import java.util.*;

public class OrderBook {
    private final TreeMap<Double, Queue<Order>> sellOrders = new TreeMap<>();
    private final TreeMap<Double, Queue<Order>> buyOrders = new TreeMap<>(Collections.reverseOrder());

    public void addOrder(Order order) {
        TreeMap<Double, Queue<Order>> bookSide = order.getSide().equals("BUY") ? buyOrders : sellOrders;
        bookSide.putIfAbsent(order.getPrice(), new PriorityQueue<>());
        bookSide.get(order.getPrice()).add(order);
    }

    public Optional<Order> matchOrder(Order newOrder) {
        TreeMap<Double, Queue<Order>> opposingBookSide = newOrder.getSide().equals("BUY") ? sellOrders : buyOrders;

        // Find the best available price for the new order
        Map.Entry<Double, Queue<Order>> bestPriceOrders = opposingBookSide.firstEntry();

        if (bestPriceOrders == null) {
            // No orders to match with
            return Optional.empty();
        }

        // Check if the new order can be matched with an order at the best available price
        if ((newOrder.getSide().equals("BUY") && newOrder.getPrice() >= bestPriceOrders.getKey()) ||
                (newOrder.getSide().equals("SELL") && newOrder.getPrice() <= bestPriceOrders.getKey())) {

            Order matchedOrder = bestPriceOrders.getValue().poll();

            // If there are no more orders at the best price, remove the price level from the order book
            if (bestPriceOrders.getValue().isEmpty()) {
                opposingBookSide.remove(bestPriceOrders.getKey());
            }

            return Optional.of(matchedOrder);
        } else {
            // The new order cannot be matched
            return Optional.empty();
        }
    }

    public Map<String, TreeMap<Double, Queue<Order>>> getFullOrderBook() {
        Map<String, TreeMap<Double, Queue<Order>>> fullOrderBook = new HashMap<>();
        fullOrderBook.put("BUY", buyOrders);
        fullOrderBook.put("SELL", sellOrders);
        return fullOrderBook;
    }

    public boolean cancelOrderById(String orderId, String orderType) {
        TreeMap<Double, Queue<Order>> bookSide = orderType.equals("BUY") ? buyOrders : sellOrders;
        for (Map.Entry<Double, Queue<Order>> entry : bookSide.entrySet()) {
            Queue<Order> ordersAtPrice = entry.getValue();
            Iterator<Order> iterator = ordersAtPrice.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                if (order.getId().equals(orderId)) {
                    iterator.remove();
                    if (ordersAtPrice.isEmpty()) {
                        bookSide.remove(entry.getKey());
                    }
                    return true;
                }
            }
        }
        return false;
    }
}