package com.demo.dispute.agent;

import com.demo.dispute.model.MerchantContext;
import org.springframework.stereotype.Component;

@Component
public class MerchantContextAgent {

    /**
     * Returns MOCK merchant and delivery data for demo purposes.
     * In a real system, this would query merchant and logistics databases.
     */
    public MerchantContext fetch(String merchantName) {
        // For merchant dispute scenarios - return problematic delivery status
        return MerchantContext.builder()
            .merchantName(merchantName)
            .deliveryStatus("NOT_DELIVERED")
            .lastKnownDeliveryAttempt("3 days ago")
            .disputeHistory(java.util.Arrays.asList("Previous dispute 1", "Previous dispute 2"))
            .riskLevel("MEDIUM")
            .disputeRate(0.05)
            .build();
    }
}
