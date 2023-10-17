package com.fardu.osl_trade_matching_engine.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Trade {

    @Id
    private String id;
    private long timestamp;
    private String instrument;
    private int quantity;
    private double price;

}
