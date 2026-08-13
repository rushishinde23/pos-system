package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.modal.Customer;
import com.zosh.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();

        // Sample data matching your JSON structure
        customer1 = new Customer();
        customer1.setId(102L);
        customer1.setFullName("Customer1");
        customer1.setEmail("customer1.@gamil.com");
        customer1.setPhone("1235567890");
        customer1.setCreatedAt(null);
        customer1.setUpdatedAt(null);

        customer2 = new Customer();
        customer2.setId(2L);
        customer2.setFullName("Customer2");
        customer2.setEmail("customer2.@gamil.com");
        customer2.setPhone("1234567890");
        customer2.setCreatedAt(null);
        customer2.setUpdatedAt(null);
    }

    @Nested
    @DisplayName("POST /api/customers")
    class CreateCustomer {

        @Test
        @DisplayName("Should create customer and verify all fields in response")
        void createCustomer_Success() throws Exception {
            // ARRANGE
            when(customerService.createCustomer(any(Customer.class))).thenReturn(customer1);

            // ACT & ASSERT
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(102L))
                    .andExpect(jsonPath("$.fullName").value("Customer1"))
                    .andExpect(jsonPath("$.email").value("customer1.@gamil.com"))
                    .andExpect(jsonPath("$.phone").value("1235567890"))
                    .andExpect(jsonPath("$.createdAt").value(customer1.getCreatedAt()))
                    .andExpect(jsonPath("$.updatedAt").value(customer1.getUpdatedAt()));

            verify(customerService, times(1)).createCustomer(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/customers/{id}")
    class UpdateCustomer {

        @Test
        @DisplayName("Should update customer and verify all fields in response")
        void updateCustomer_Success() throws Exception {
            // ARRANGE
            when(customerService.updateCustomer(eq(102L), any(Customer.class))).thenReturn(customer1);

            // ACT & ASSERT
            mockMvc.perform(put("/api/customers/102")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(102L))
                    .andExpect(jsonPath("$.fullName").value("Customer1"))
                    .andExpect(jsonPath("$.email").value("customer1.@gamil.com"))
                    .andExpect(jsonPath("$.phone").value("1235567890"))
                    .andExpect(jsonPath("$.createdAt").value(customer1.getCreatedAt()))
                    .andExpect(jsonPath("$.updatedAt").value(customer1.getUpdatedAt()));

            verify(customerService, times(1)).updateCustomer(eq(102L), any(Customer.class));
        }

        @Test
        @DisplayName("Should throw Exception when customer update fails")
        void updateCustomer_NotFound_ThrowsException() throws Exception {
            // ARRANGE
            when(customerService.updateCustomer(eq(999L), any(Customer.class)))
                    .thenThrow(new Exception("Customer not found with id 999"));

            // ACT & ASSERT
            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(put("/api/customers/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer1)))
            );

            assertEquals("Customer not found with id 999", exception.getCause().getMessage());
            verify(customerService, times(1)).updateCustomer(eq(999L), any(Customer.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/customers/{id}")
    class DeleteCustomer {

        @Test
        @DisplayName("Should delete customer and return success message")
        void deleteCustomer_Success() throws Exception {
            // ARRANGE
            doNothing().when(customerService).deleteCustomer(102L);

            // ACT & ASSERT
            mockMvc.perform(delete("/api/customers/102"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Customer deleted successfully"));

            verify(customerService, times(1)).deleteCustomer(102L);
        }

        @Test
        @DisplayName("Should throw Exception when customer deletion fails")
        void deleteCustomer_NotFound_ThrowsException() throws Exception {
            // ARRANGE
            doThrow(new Exception("Customer not found with id 999"))
                    .when(customerService).deleteCustomer(999L);

            // ACT & ASSERT
            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(delete("/api/customers/999"))
            );

            assertEquals("Customer not found with id 999", exception.getCause().getMessage());
            verify(customerService, times(1)).deleteCustomer(999L);
        }
    }

    @Nested
    @DisplayName("GET /api/customers")
    class GetAllCustomers {

        @Test
        @DisplayName("Should return list of customers and verify all fields in each item")
        void getAllCustomers_Success() throws Exception {
            // ARRANGE
            List<Customer> customerList = List.of(customer2, customer1);
            when(customerService.getAllCustomers()).thenReturn(customerList);

            // ACT & ASSERT
            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))

                    // First array element (customer2)
                    .andExpect(jsonPath("$[0].id").value(2L))
                    .andExpect(jsonPath("$[0].fullName").value("Customer2"))
                    .andExpect(jsonPath("$[0].email").value("customer2.@gamil.com"))
                    .andExpect(jsonPath("$[0].phone").value("1234567890"))
                    .andExpect(jsonPath("$[0].createdAt").value(customer2.getCreatedAt()))
                    .andExpect(jsonPath("$[0].updatedAt").value(customer2.getUpdatedAt()))

                    // Second array element (customer1)
                    .andExpect(jsonPath("$[1].id").value(102L))
                    .andExpect(jsonPath("$[1].fullName").value("Customer1"))
                    .andExpect(jsonPath("$[1].email").value("customer1.@gamil.com"))
                    .andExpect(jsonPath("$[1].phone").value("1235567890"))
                    .andExpect(jsonPath("$[1].createdAt").value(customer1.getCreatedAt()))
                    .andExpect(jsonPath("$[1].updatedAt").value(customer1.getUpdatedAt()));

            verify(customerService, times(1)).getAllCustomers();
        }
    }

    @Nested
    @DisplayName("GET /api/customers/keyword?keyword={keyword}")
    class SearchCustomers {

        @Test
        @DisplayName("Should return filtered list of customers based on search keyword")
        void searchCustomers_Success() throws Exception {
            // ARRANGE
            List<Customer> searchResults = List.of(customer1);
            when(customerService.searchCustomer("Customer1")).thenReturn(searchResults);

            // ACT & ASSERT
            mockMvc.perform(get("/api/customers/keyword")
                            .param("keyword", "Customer1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(102L))
                    .andExpect(jsonPath("$[0].fullName").value("Customer1"))
                    .andExpect(jsonPath("$[0].email").value("customer1.@gamil.com"))
                    .andExpect(jsonPath("$[0].phone").value("1235567890"))
                    .andExpect(jsonPath("$[0].createdAt").value(customer1.getCreatedAt()))
                    .andExpect(jsonPath("$[0].updatedAt").value(customer1.getUpdatedAt()));

            verify(customerService, times(1)).searchCustomer("Customer1");
        }
    }
}