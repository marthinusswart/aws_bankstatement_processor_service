package com.mattswart.bankstatementprocessor.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.mattswart.bankstatementprocessor.dto.BankstatementTransaction;

@Service
public class BankstatementProcessorMessagePublisher {
    @Autowired
    private KafkaTemplate<String, Object> template;

    public void sendBankstatementTransaction(BankstatementTransaction bankstatementTransaction) {
        try {
            CompletableFuture<SendResult<String, Object>> future = template.send("bankstatement_processor_transaction",
                    bankstatementTransaction);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    // System.out.println("Sent message=[" + gdpDetailRecord.toString() +
                    // "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                    bankstatementTransaction.toString() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            System.out.println("ERROR : " + ex.getMessage());
        }
    }

}
