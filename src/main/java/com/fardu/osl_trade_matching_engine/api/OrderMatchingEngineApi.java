package com.fardu.osl_trade_matching_engine.api;

import com.fardu.osl_trade_matching_engine.engine.OrderBookManager;
import com.fardu.osl_trade_matching_engine.models.Order;
import com.fardu.osl_trade_matching_engine.models.Trade;
import com.fardu.osl_trade_matching_engine.persistence.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OrderMatchingEngineApi {

    private final OrderBookManager orderBookManager;
    private final TradeService tradeService;

    @PostMapping("/new_order")
    public ResponseEntity<Object> placeNewOrder(@RequestParam String instrument, @RequestParam String side, @RequestParam double price, @RequestParam int quantity){
        try{
            if (instrument.equals("BTC") || instrument.equals("ETH") || instrument.equals("USDT")){
                if (side.equals("BUY") || side.equals("SELL")){
                    Order order = new Order(UUID.randomUUID().toString(), System.currentTimeMillis(), side, instrument, price, quantity);
                    orderBookManager.sortIncomingOrders(order);
                    return new ResponseEntity<>(order, HttpStatusCode.valueOf(200));
                } else {
                    return new ResponseEntity<>("Order could not be processed, check the 'type' field (BUY/SELL).", HttpStatusCode.valueOf(500));
                }
            } else {
                return new ResponseEntity<>("Order could not be processed, check the 'instrument' field (BTC/ETH/USDT).", HttpStatusCode.valueOf(500));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/retrieve_orderbook")
    public ResponseEntity<Object> retrieveCurrentOrderbook(@RequestParam String instrument){
        Map<String, TreeMap<Double, Queue<Order>>> response;
        try{
            if (instrument.equals("USDT") || instrument.equals("BTC") || instrument.equals("ETH")){
                response = orderBookManager.fullOrderBookSnapshot(instrument);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>("Error retrieving full orderbook snapshot for " + instrument + ", invalid symbol", HttpStatusCode.valueOf(500));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error retrieving full orderbook snapshot for " + instrument + ", internal error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @DeleteMapping("/cancel_existing_order")
    public ResponseEntity<String> cancelExistingOrder(@RequestParam String side, @RequestParam String id, @RequestParam String instrument){
        try{
            if ((side.equals("BUY") || side.equals("SELL")) && (instrument.equals("BTC") || instrument.equals("ETH") || instrument.equals("USDT"))){
                String response =  orderBookManager.cancelExistingOrder(instrument, side, id);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else{
                return new ResponseEntity<>("Invalid parameters passed in the request", HttpStatusCode.valueOf(200));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/get_executed_trades")
    public ResponseEntity<Object> retrieveExecutedTrades(@RequestParam String instrument){
        try {
            if (instrument.equals("BTC") || instrument.equals("ETH") || instrument.equals("USDT")) {
                List<Trade> executedTrades = tradeService.findTradesByInstrument(instrument);
                return new ResponseEntity<>(executedTrades, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>("Invalid parameters passed in the request", HttpStatusCode.valueOf(200));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

}
