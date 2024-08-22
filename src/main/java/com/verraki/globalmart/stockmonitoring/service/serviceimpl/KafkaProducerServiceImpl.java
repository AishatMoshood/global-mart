package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import com.verraki.globalmart.stockmonitoring.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture<String> sendReorderMessage(String topic, String encryptedMessage) {
        CompletableFuture<String> future = new CompletableFuture<>();

        kafkaTemplate.send(topic, encryptedMessage)
                .thenApply(result -> {
                    String successMessage = "Reorder message sent successfully: " + encryptedMessage +
                            " with offset " + result.getRecordMetadata().offset();
                    log.info(successMessage);
                    return successMessage;
                })
                .exceptionally(ex -> {
                    String failureMessage = "Failed to send reorder message: " + encryptedMessage;
                    log.error(failureMessage, ex);
                    future.completeExceptionally(ex);
                    return failureMessage;
                })
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        future.complete(result);
                    }
                });

        return future;
    }
}