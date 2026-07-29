package com.zosh.modal;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CustomerTest {


    @Test
    void testCustomer(){


        Customer customer =
                Customer.builder()
                        .id(1L)
                        .fullName("Ashish")
                        .email("ashish@gmail.com")
                        .phone("9999999999")
                        .build();


        assertEquals("Ashish",
                customer.getFullName());


        assertEquals(
                "ashish@gmail.com",
                customer.getEmail());


        customer.setPhone("8888888888");


        assertEquals(
                "8888888888",
                customer.getPhone());

    }

}