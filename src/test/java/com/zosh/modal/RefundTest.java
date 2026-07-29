package com.zosh.modal;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class RefundTest {


    @Test
    void testRefund(){


        Refund refund =
                Refund.builder()
                        .id(1L)
                        .amount(500.0)
                        .reason("Damaged")
                        .build();



        assertEquals(
                500.0,
                refund.getAmount());


        assertEquals(
                "Damaged",
                refund.getReason());


    }


    @Test
    void testRefundCreate(){


        Refund refund=new Refund();


        refund.onCreate();


        assertNotNull(
                refund.getCreatedAt());


    }

}