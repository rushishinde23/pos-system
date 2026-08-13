package com.zosh.service.impl;

import com.zosh.exceptions.UserException;
import com.zosh.modal.Branch;
import com.zosh.modal.Order;
import com.zosh.modal.Refund;
import com.zosh.modal.User;
import com.zosh.payload.dto.RefundDTO;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.RefundRepository;
import com.zosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private RefundServiceImpl refundService;

    private User cashier;
    private Order order;
    private Branch branch;
    private RefundDTO refundDTO;
    private Refund refund;

    @BeforeEach
    void setUp() {
        cashier = new User();
        cashier.setId(1L);
        branch = new Branch();
        branch.setId(1L);

        order = new Order();
        order.setId(10L);
        order.setBranch(branch);

        refundDTO = new RefundDTO();
        refundDTO.setOrderId(10L);
        refundDTO.setReason("Damaged item");
        refundDTO.setAmount(20.0);
        refundDTO.setCreatedAt(LocalDateTime.now());

        refund = Refund.builder().order(order).cashier(cashier).branch(branch)
                .reason("Damaged item").amount(20.0).build();
    }

    @Test
    void createRefund_success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        RefundDTO result = refundService.createRefund(refundDTO);

        assertEquals("Damaged item", result.getReason());
    }

    @Test
    void createRefund_orderNotFound_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(orderRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> refundService.createRefund(refundDTO));
    }

    @Test
    void getAllRefunds_returnsList() throws Exception {
        when(refundRepository.findAll()).thenReturn(Arrays.asList(refund, refund));
        assertEquals(2, refundService.getAllRefunds().size());
    }

    @Test
    void getRefundByCashier_returnsList() throws Exception {
        when(refundRepository.findByCashierId(1L)).thenReturn(List.of(refund));
        assertEquals(1, refundService.getRefundByCashier(1L).size());
    }

    @Test
    void getRefundByShiftReport_returnsList() throws Exception {
        when(refundRepository.findByShiftReportId(5L)).thenReturn(List.of(refund));
        assertEquals(1, refundService.getRefundByShiftReport(5L).size());
    }

    @Test
    void getRefundByCashierAndDateRange_returnsList() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(refundRepository.findByCashierIdAndCreatedAtBetween(1L, start, end)).thenReturn(List.of(refund));

        List<RefundDTO> result = refundService.getRefundByCashierAndDateRange(1L, start, end);

        assertEquals(1, result.size());
    }

    @Test
    void getRefundByBranch_returnsList() throws Exception {
        when(refundRepository.findByBranchId(1L)).thenReturn(List.of(refund));
        assertEquals(1, refundService.getRefundByBranch(1L).size());
    }

    @Test
    void getRefundById_success() throws Exception {
        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        RefundDTO result = refundService.getRefundById(1L);
        assertEquals("Damaged item", result.getReason());
    }

    @Test
    void getRefundById_notFound_throws() {
        when(refundRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> refundService.getRefundById(99L));
    }

    @Test
    void deleteRefund_success() throws Exception {
        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        refundService.deleteRefund(1L);
        verify(refundRepository).deleteById(1L);
    }

    @Test
    void deleteRefund_notFound_throws() {
        when(refundRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> refundService.deleteRefund(99L));
    }
}