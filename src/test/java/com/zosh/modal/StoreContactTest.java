package com.zosh.modal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoreContactTest {


    @Test
    void testStoreContact(){

        StoreContact contact =
                StoreContact.builder()
                        .address("Pune")
                        .phone("9999999999")
                        .email("test@gmail.com")
                        .build();


        assertEquals("Pune",contact.getAddress());
        assertEquals("9999999999",contact.getPhone());
        assertEquals("test@gmail.com",contact.getEmail());


        contact.setPhone("8888888888");

        assertEquals("8888888888",
                contact.getPhone());

    }
}