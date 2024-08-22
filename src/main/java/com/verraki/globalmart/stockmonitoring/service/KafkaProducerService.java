package com.verraki.globalmart.stockmonitoring.service;

import org.springframework.util.concurrent.ListenableFuture;

public interface KafkaProducerService {
    ListenableFuture<String> sendReorderMessage(String topic, String encryptedMessage);
}