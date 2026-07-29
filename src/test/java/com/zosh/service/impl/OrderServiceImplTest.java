package com.zosh.service.impl;

import com.zosh.domain.PaymentType;
import com.zosh.exceptions.UserException;
import com.zosh.modal.Branch;
import com.zosh.modal.Category;
import com.zosh.modal.Customer;
import com.zosh.modal.Order;
import com.zosh.modal.Product;
import com.zosh.modal.User;
import com.zosh.payload.dto.OrderDTO;
import com.zosh.payload.dto.OrderItemDTO;
import com.zosh.repository.OrderItemRepository;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.service.UserService;
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
class OrderServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User cashier;
    private Branch branch;
    private Product product;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(1L);

        cashier = new User();
        cashier.setId(1L);
        cashier.setBranch(branch);

        product = new Product();
        product.setId(1L);
        product.setSellingPrice(10.0);
        product.setCategory(Category.builder().name("Snacks").build());

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);

        orderDTO = new OrderDTO();
        orderDTO.setPaymentType(PaymentType.CASH);
        orderDTO.setItems(List.of(itemDTO));
    }

    private Order newOrder() {
        Order order = new Order();
        order.setBranch(branch);
        order.setCashier(cashier);
        order.setItems(List.of());
        return order;
    }

    @Test
    void createOrder_success_calculatesTotal() throws Exception {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertEquals(20.0, result.getTotalAmount());
    }

    @Test
    void createOrder_noBranch_throws() throws UserException {
        cashier.setBranch(null);
        when(userService.getCurrentUser()).thenReturn(cashier);

        assertThrows(Exception.class, () -> orderService.createOrder(orderDTO));
    }

    @Test
    void getOrderById_success() throws Exception {
        Order order = newOrder();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
    }

    @Test
    void getOrderById_notFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getOrdersByBranch_filtersByCustomerCashierAndPaymentType() throws Exception {
        Customer customer = new Customer();
        customer.setId(5L);

        Order matching = newOrder();
        matching.setCustomer(customer);
        matching.setCashier(cashier);
        matching.setPaymentType(PaymentType.CASH);

        Order nonMatching = newOrder();
        nonMatching.setPaymentType(PaymentType.CARD);

        when(orderRepository.findByBranchId(1L)).thenReturn(Arrays.asList(matching, nonMatching));

        List<OrderDTO> result = orderService.getOrdersByBranch(1L, 5L, 1L, PaymentType.CASH, null);

        assertEquals(1, result.size());
    }

    @Test
    void getOrderByCashier_returnsList() {
        when(orderRepository.findByCashierId(1L)).thenReturn(List.of(newOrder()));
        assertEquals(1, orderService.getOrderByCashier(1L).size());
    }

    @Test
    void deleteOrder_success() throws Exception {
        Order order = newOrder();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_notFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> orderService.deleteOrder(99L));
    }

    @Test
    void getTodayOrdersByBranch_returnsList() throws Exception {
        when(orderRepository.findByBranchIdAndCreatedAtBetween(eq(1L), any(), any()))
                .thenReturn(List.of(newOrder()));

        List<OrderDTO> result = orderService.getTodayOrdersByBranch(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getOrdersByCustomerId_returnsList() throws Exception {
        when(orderRepository.findByCustomerId(5L)).thenReturn(List.of(newOrder()));
        assertEquals(1, orderService.getOrdersByCustomerId(5L).size());
    }

    @Test
    void getTop5RecentOrdesBranchId_returnsList() throws Exception {
        when(orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(newOrder()));
        assertEquals(1, orderService.getTop5RecentOrdesBranchId(1L).size());
    }
}