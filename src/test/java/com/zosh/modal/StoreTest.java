package com.zosh.modal;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class StoreTest {


    @Test
    void testStore(){


        Store store =
                new Store();


        store.setBrand("Zosh Store");


        assertEquals(
                "Zosh Store",
                store.getBrand());


    }



    @Test
    void testStoreLifecycle(){

        Store store=new Store();


        store.onCreate();


        assertNotNull(
                store.getCreatedAt());

        assertNotNull(
                store.getStatus());



        store.onUpdate();


        assertNotNull(
                store.getUpdatedAt());

    }


}