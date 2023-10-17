package com.fardu.osl_trade_matching_engine.persistence;

import com.fardu.osl_trade_matching_engine.models.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, String> {
    List<Trade> findByInstrument(String instrument);
}