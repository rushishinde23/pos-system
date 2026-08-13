package com.zosh.modal;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class InventoryTest {


    @Test
    void testInventory(){


        Inventory inventory =
                Inventory.builder()
                        .id(1L)
                        .quantity(20)
                        .build();


        assertEquals(20,
                inventory.getQuantity());


    }



    @Test
    void testInventoryUpdate(){

        Inventory inventory=new Inventory();


        inventory.onUpdate();


        assertNotNull(
                inventory.getLastUpdate());

    }

}