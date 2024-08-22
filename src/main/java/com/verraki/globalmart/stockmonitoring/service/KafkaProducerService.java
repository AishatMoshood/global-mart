package com.verraki.globalmart.stockmonitoring.service;

import java.util.concurrent.CompletableFuture;

public interface KafkaProducerService {
    CompletableFuture<String> sendReorderMessage(String topic, String encryptedMessage);
}