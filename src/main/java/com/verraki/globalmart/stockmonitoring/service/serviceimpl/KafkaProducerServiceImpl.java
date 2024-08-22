package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import com.verraki.globalmart.stockmonitoring.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ListenableFuture;
import org.springframework.kafka.support.ListenableFutureCallback;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureAdapter;


@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ListenableFuture<String> sendReorderMessage(String topic, String encryptedMessage) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, encryptedMessage);
        ListenableFuture<String> resultFuture = new ListenableFutureAdapter<>();

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                String successMessage = "Reorder message sent successfully: " + encryptedMessage 
                                        + " with offset " + result.getRecordMetadata().offset();
                log.info(successMessage);
                resultFuture.set(successMessage);
            }

            @Override
            public void onFailure(Throwable ex) {
                String failureMessage = "Failed to send reorder message: " + encryptedMessage;
                log.error(failureMessage, ex);
                resultFuture.setException(ex);
            }
        });

        return resultFuture;
    }
}