package com.zosh.service.impl;

import com.zosh.modal.Customer;
import com.zosh.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFullName("Jane Doe");
        customer.setEmail("jane@example.com");
        customer.setPhone("1234567890");
    }

    @Test
    void createCustomer_savesAndReturns() {
        when(customerRepository.save(customer)).thenReturn(customer);
        Customer result = customerService.createCustomer(customer);
        assertEquals(customer, result);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_success_updatesFields() throws Exception {
        Customer existing = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        Customer result = customerService.updateCustomer(1L, customer);

        assertEquals("Jane Doe", result.getFullName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
    }

    @Test
    void updateCustomer_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> customerService.updateCustomer(99L, customer));
    }

    @Test
    void deleteCustomer_success_deletes() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customerService.deleteCustomer(1L);
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> customerService.deleteCustomer(99L));
    }

    @Test
    void getCustomer_success() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        assertEquals(customer, customerService.getCustomer(1L));
    }

    @Test
    void getCustomer_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> customerService.getCustomer(99L));
    }

    @Test
    void getAllCustomers_returnsList() throws Exception {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer, new Customer()));
        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
    }

    @Test
    void searchCustomer_returnsMatches() throws Exception {
        when(customerRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase("jane", "jane"))
                .thenReturn(List.of(customer));
        List<Customer> result = customerService.searchCustomer("jane");
        assertEquals(1, result.size());
    }
}