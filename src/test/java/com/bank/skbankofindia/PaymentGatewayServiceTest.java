package com.bank.skbankofindia;

import com.bank.skbankofindia.service.PaymentGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentGatewayServiceTest {

    private PaymentGatewayService paymentGatewayService;

    @BeforeEach
    public void setUp() {
        paymentGatewayService = new PaymentGatewayService();
    }

    @Test
    @DisplayName("Should verify webhook signature computation correctly")
    public void testWebhookSignatureVerification() {
        String payload = "{\"event\":\"payment.authorized\",\"order_id\":\"order_123\"}";
        // Signature test
        boolean isValid = paymentGatewayService.verifyWebhookSignature(payload, "invalid_signature");
        assertFalse(isValid);
    }
}
