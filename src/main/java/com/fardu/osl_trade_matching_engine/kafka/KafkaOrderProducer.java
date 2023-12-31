package com.fardu.osl_trade_matching_engine.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaOrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, String topic) {
        System.out.println(message);
        this.kafkaTemplate.send(topic, message);
    }
}