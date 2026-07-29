package com.zosh.modal;

import com.zosh.domain.PaymentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class OrderTest {


    @Test
    void testOrder(){

        Branch branch = new Branch();
        User cashier = new User();
        Customer customer = new Customer();

        OrderItem item = new OrderItem();


        Order order =
                Order.builder()
                        .id(10L)
                        .totalAmount(5000.0)
                        .branch(branch)
                        .cashier(cashier)
                        .customer(customer)
                        .items(List.of(item))
                        .paymentType(PaymentType.CASH)
                        .build();



        assertEquals(10L,
                order.getId());


        assertEquals(5000.0,
                order.getTotalAmount());


        assertEquals(branch,
                order.getBranch());


        assertEquals(cashier,
                order.getCashier());


        assertEquals(customer,
                order.getCustomer());


        assertEquals(1,
                order.getItems().size());


        assertEquals(PaymentType.CASH,
                order.getPaymentType());



        order.setTotalAmount(6000.0);

        order.setBranch(new Branch());

        order.setCashier(new User());

        order.setCustomer(new Customer());

        order.setItems(List.of(new OrderItem()));

        order.setPaymentType(PaymentType.CARD);



        assertEquals(6000.0,
                order.getTotalAmount());

        assertNotNull(order.getBranch());

        assertNotNull(order.getCashier());

        assertNotNull(order.getCustomer());

        assertNotNull(order.getItems());

        assertEquals(
                PaymentType.CARD,
                order.getPaymentType());

    }



    @Test
    void testOrderCreate(){

        Order order=new Order();

        order.onCreate();


        assertNotNull(
                order.getCreatedAt());

    }


}