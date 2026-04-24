package com.demo.dispute.agent;

import com.demo.dispute.model.TransactionData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionDataAgent {

    /**
     * Returns MOCK transaction data for demo purposes.
     * In a real system, this would query a transaction database.
     */
    public TransactionData fetch(String transactionId) {
        // For fraud scenario - return suspicious transaction data
        if (transactionId.contains("FRAUD") || transactionId.hashCode() % 2 == 0) {
            return TransactionData.builder()
                .transactionId(transactionId)
                .amount(25000.0)
                .currency("INR")
                .location("Unknown Foreign Country")
                .merchantName("Foreign Merchant XYZ")
                .timestamp(LocalDateTime.now().minusHours(2))
                .hasTravelHistory(false)
                .isFirstTimeLocation(true)
                .cardType("Debit Card")
                .deviceInfo("Unknown device, IP from foreign location")
                .build();
        }
        
        // For merchant scenario - return normal transaction data
        return TransactionData.builder()
            .transactionId(transactionId)
            .amount(8000.0)
            .currency("INR")
            .location("Mumbai, India")
            .merchantName("QuickShop India")
            .timestamp(LocalDateTime.now().minusDays(14))
            .hasTravelHistory(true)
            .isFirstTimeLocation(false)
            .cardType("Credit Card")
            .deviceInfo("Customer's registered device")
            .build();
    }
}
