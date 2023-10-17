package com.fardu.osl_trade_matching_engine.persistence;

import com.fardu.osl_trade_matching_engine.models.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public Trade saveTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public void createAndSaveTrade(String instrument, int quantity, double price){
        Trade newTrade = new Trade(UUID.randomUUID().toString(), System.currentTimeMillis(), instrument, quantity, price);
        saveTrade(newTrade);
    }

    public List<Trade> findTradesByInstrument(String instrument) {
        return tradeRepository.findByInstrumentOrderByTimestampDesc(instrument);
    }

}