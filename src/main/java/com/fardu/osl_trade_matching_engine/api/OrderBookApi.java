package com.fardu.osl_trade_matching_engine.api;

import com.fardu.osl_trade_matching_engine.engine.OrderBookManager;
import com.fardu.osl_trade_matching_engine.models.Order;
import com.fardu.osl_trade_matching_engine.models.Trade;
import com.fardu.osl_trade_matching_engine.persistence.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OrderBookApi {


    private final OrderBookManager orderBookManager;
    private final TradeService tradeService;

    @PostMapping("/new_order")
    public ResponseEntity<String> placeNewOrder(@RequestBody Order order){
        try{
            orderBookManager.sortIncomingOrders(order);
            return new ResponseEntity<>("Order successfully sent to order matching engine.", HttpStatusCode.valueOf(200));
        } catch (HttpMessageNotReadableException e){
            return new ResponseEntity<>("Order could not be processed, check if request has extra/missing fields.", HttpStatusCode.valueOf(500));
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/retrieve_orderbook")
    public ResponseEntity<Object> retrieveCurrentOrderbook(@RequestParam String symbol){

        Map<String, TreeMap<Double, Queue<Order>>> response;

        try{
            if (symbol.equals("USDT") || symbol.equals("BTC") || symbol.equals("ETH")){
                response = orderBookManager.fullOrderBookSnapshot(symbol);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>("Error retrieving full orderbook snapshot for " + symbol + ", invalid symbol", HttpStatusCode.valueOf(500));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error retrieving full orderbook snapshot for " + symbol + ", internal error occurred", HttpStatusCode.valueOf(500));
        }

    }

    @DeleteMapping("/cancel_existing_order")
    public ResponseEntity<String> cancelExistingOrder(@RequestParam String side, @RequestParam String id, @RequestParam String symbol){

        try{
            if ((side.equals("BUY") || side.equals("SELL")) && (symbol.equals("BTC") || symbol.equals("ETH") || symbol.equals("USDT"))){
                String response =  orderBookManager.cancelExistingOrder(symbol, side, id);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            }
            else{
                return new ResponseEntity<>("Invalid parameters passed in the request", HttpStatusCode.valueOf(200));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/get_executed_trades")
    public ResponseEntity<Object> retrieveExecutedTrades(@RequestParam String symbol){
        try {
            if (symbol.equals("BTC") || symbol.equals("ETH") || symbol.equals("USDT")) {
                List<Trade> executedTrades = tradeService.findTradesByInstrument(symbol);
                return new ResponseEntity<>(executedTrades, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>("Invalid parameters passed in the request", HttpStatusCode.valueOf(200));
            }
        } catch (Exception e){
            return new ResponseEntity<>("Internal server error occurred", HttpStatusCode.valueOf(500));
        }
    }

}
