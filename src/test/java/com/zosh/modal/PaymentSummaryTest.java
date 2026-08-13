package com.zosh.modal;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PaymentSummaryTest {


    @Test
    void testPaymentSummary(){


        PaymentSummary summary =
                PaymentSummary.builder()
                        .totalAmount(1000.0)
                        .transactionCount(5)
                        .percentage(50.0)
                        .build();


        assertEquals(
                1000.0,
                summary.getTotalAmount());


        assertEquals(
                5,
                summary.getTransactionCount());

    }

}