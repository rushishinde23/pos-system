package com.zosh.modal;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class ProductTest {


    @Test
    void testProduct(){

        Product product =
                Product.builder()
                        .id(1L)
                        .name("Laptop")
                        .sku("LP100")
                        .mrp(50000.0)
                        .sellingPrice(45000.0)
                        .brand("Dell")
                        .build();


        assertEquals("Laptop",
                product.getName());

        assertEquals("LP100",
                product.getSku());


        product.setBrand("HP");

        assertEquals("HP",
                product.getBrand());

    }


    @Test
    void testProductPrePersist(){

        Product product=new Product();

        product.onCreate();


        assertNotNull(product.getCreatedAt());

    }



}