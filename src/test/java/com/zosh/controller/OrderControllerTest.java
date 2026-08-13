package com.zosh.controller;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import com.zosh.payload.dto.OrderDTO;
import com.zosh.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO mockOrderDTO;

    @BeforeEach
    void setUp() {
        mockOrderDTO = new OrderDTO();
        // Set basic mock properties if needed (e.g., mockOrderDTO.setId(1L));
    }

    @Nested
    @DisplayName("POST /api/orders - createOrder")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully and return 201 CREATED")
        void createOrder_Success() throws Exception {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(mockOrderDTO);

            ResponseEntity<OrderDTO> response = orderController.createOrder(mockOrderDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(mockOrderDTO, response.getBody());
            verify(orderService, times(1)).createOrder(any(OrderDTO.class));
        }

        @Test
        @DisplayName("Should propagate exception when orderService throws an exception")
        void createOrder_ExceptionThrown() throws Exception {
            when(orderService.createOrder(any(OrderDTO.class)))
                    .thenThrow(new RuntimeException("Failed to create order"));

            Exception exception = assertThrows(RuntimeException.class, () ->
                    orderController.createOrder(mockOrderDTO)
            );

            assertEquals("Failed to create order", exception.getMessage());
            verify(orderService, times(1)).createOrder(any(OrderDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId} - getOrderById")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Should return order and 200 OK when valid order ID is given")
        void getOrderById_Success() throws Exception {
            Long orderId = 100L;
            when(orderService.getOrderById(orderId)).thenReturn(mockOrderDTO);

            ResponseEntity<OrderDTO> response = orderController.getOrderById(orderId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockOrderDTO, response.getBody());
            verify(orderService, times(1)).getOrderById(orderId);
        }

        @Test
        @DisplayName("Should propagate exception when order is not found")
        void getOrderById_NotFound() throws Exception {
            Long orderId = 999L;
            when(orderService.getOrderById(orderId)).thenThrow(new Exception("Order not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    orderController.getOrderById(orderId)
            );

            assertEquals("Order not found", exception.getMessage());
            verify(orderService, times(1)).getOrderById(orderId);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/branch/{branchId} - getOrdersByBranch")
    class GetOrdersByBranchTests {

        @Test
        @DisplayName("Should return list of orders when filtered by branch and optional params")
        void getOrdersByBranch_Success() throws Exception {
            Long branchId = 1L;
            Long customerId = 2L;
            Long cashierId = 3L;
            PaymentType paymentType = PaymentType.CASH;
            OrderStatus orderStatus = OrderStatus.COMPLETED;

            List<OrderDTO> orders = List.of(mockOrderDTO);

            when(orderService.getOrdersByBranch(branchId, customerId, cashierId, paymentType, orderStatus))
                    .thenReturn(orders);

            ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByBranch(
                    branchId, customerId, cashierId, paymentType, orderStatus
            );

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(orderService, times(1))
                    .getOrdersByBranch(branchId, customerId, cashierId, paymentType, orderStatus);
        }

        @Test
        @DisplayName("Should return empty list when no orders match branch criteria")
        void getOrdersByBranch_EmptyList() throws Exception {
            Long branchId = 1L;
            when(orderService.getOrdersByBranch(eq(branchId), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByBranch(
                    branchId, null, null, null, null
            );

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());
            verify(orderService, times(1))
                    .getOrdersByBranch(branchId, null, null, null, null);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/cashier/{cashierId} - getOrdersByCashier")
    class GetOrdersByCashierTests {

        @Test
        @DisplayName("Should return orders associated with given cashier ID")
        void getOrdersByCashier_Success() {
            Long cashierId = 5L;
            List<OrderDTO> orders = List.of(mockOrderDTO);

            when(orderService.getOrderByCashier(cashierId)).thenReturn(orders);

            ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByCashier(cashierId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(orderService, times(1)).getOrderByCashier(cashierId);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/today/branch/{branchId} - getTodaysOrders")
    class GetTodaysOrdersTests {

        @Test
        @DisplayName("Should return today's orders for the given branch ID")
        void getTodaysOrders_Success() throws Exception {
            Long branchId = 1L;
            List<OrderDTO> orders = List.of(mockOrderDTO);

            when(orderService.getTodayOrdersByBranch(branchId)).thenReturn(orders);

            ResponseEntity<List<OrderDTO>> response = orderController.getTodaysOrders(branchId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(orderService, times(1)).getTodayOrdersByBranch(branchId);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/customer/{customerId} - getOrdersByCustomerId")
    class GetOrdersByCustomerIdTests {

        @Test
        @DisplayName("Should return orders for given customer ID")
        void getOrdersByCustomerId_Success() throws Exception {
            Long customerId = 10L;
            List<OrderDTO> orders = List.of(mockOrderDTO);

            when(orderService.getOrdersByCustomerId(customerId)).thenReturn(orders);

            ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByCustomerId(customerId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(orderService, times(1)).getOrdersByCustomerId(customerId);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/recent/branch/{branchId} - getRecentOrdersByBranch")
    class GetRecentOrdersByBranchTests {

        @Test
        @DisplayName("Should return top 5 recent orders for given branch ID")
        void getRecentOrdersByBranch_Success() throws Exception {
            Long branchId = 1L;
            List<OrderDTO> orders = List.of(mockOrderDTO);

            when(orderService.getTop5RecentOrdesBranchId(branchId)).thenReturn(orders);

            ResponseEntity<List<OrderDTO>> response = orderController.getRecentOrdersByBranch(branchId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(orderService, times(1)).getTop5RecentOrdesBranchId(branchId);
        }
    }
}