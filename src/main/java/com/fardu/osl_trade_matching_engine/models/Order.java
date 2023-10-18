package com.fardu.osl_trade_matching_engine.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Comparable<Order> {
    private String id;
    private long timestamp;
    private String side;
    private String instrument;
    private double price;
    private int quantity;

    // Orders are primarily sorted by price, then by time of placement
    @Override
    public int compareTo(Order other) {
        int priceComparison = Double.compare(this.price, other.price);
        if (priceComparison == 0) {
            // Assuming the ID is a timestamp or sequence that represents order time
            return this.id.compareTo(other.id);
        } else {
            return priceComparison;
        }
    }
}
