package com.fardu.osl_trade_matching_engine.simulation;

import com.fardu.osl_trade_matching_engine.models.Order;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.Properties;
import java.util.UUID;

public class Test {

    private final static String TOPIC = "orders";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {

        // Configure the Producer
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", OrderSerializer.class.getName());

        Producer<String, Order> producer = new KafkaProducer<>(props);

        try {
            // Generate orders and send them as messages
            for (int i = 0; i < 300; i++) {
                String id = UUID.randomUUID().toString();
                String type = (i % 2 == 0) ? "BUY" : "SELL";
                int multiplier = (i < 100) ? 1 : (i < 200) ? 2000 : 25000;
                String instrument = (i < 100) ? "USDT" : (i < 200) ? "ETH" : "BTC";
                double price = Math.random() * 100 + multiplier;
                int quantity = (int) (Math.random() * 100);

                Order order = new Order();
                order.setId(id);
                order.setSide(type);
                order.setInstrument(instrument);
                order.setPrice(price);
                order.setQuantity(quantity);

                producer.send(new ProducerRecord<>(TOPIC, id, order));
            }
        } finally {
            producer.close();
        }
    }

    public static class OrderSerializer implements Serializer<Order> {
        private final Gson gson = new Gson();

        @Override
        public byte[] serialize(String topic, Order order) {
            return gson.toJson(order).getBytes();
        }
    }
}
