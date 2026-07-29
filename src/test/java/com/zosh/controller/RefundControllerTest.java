package com.zosh.controller;

import com.zosh.payload.dto.RefundDTO;
import com.zosh.service.RefundService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundControllerTest {

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundController refundController;

    private RefundDTO mockRefundDTO;

    @BeforeEach
    void setUp() {
        mockRefundDTO = new RefundDTO();
    }

    @Nested
    @DisplayName("POST /api/refunds - createdRefund")
    class CreatedRefundTests {

        @Test
        @DisplayName("Should create refund successfully and return 200 OK")
        void createdRefund_Success() throws Exception {
            when(refundService.createRefund(any(RefundDTO.class))).thenReturn(mockRefundDTO);

            ResponseEntity<RefundDTO> response = refundController.createdRefund(mockRefundDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockRefundDTO, response.getBody());

            verify(refundService, times(1)).createRefund(any(RefundDTO.class));
        }

        @Test
        @DisplayName("Should propagate exception when refund processing fails")
        void createdRefund_Exception() throws Exception {
            when(refundService.createRefund(any(RefundDTO.class)))
                    .thenThrow(new RuntimeException("Refund processing failed"));

            Exception exception = assertThrows(RuntimeException.class, () ->
                    refundController.createdRefund(mockRefundDTO)
            );

            assertEquals("Refund processing failed", exception.getMessage());
            verify(refundService, times(1)).createRefund(any(RefundDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/refunds - getAllRefund")
    class GetAllRefundTests {

        @Test
        @DisplayName("Should return list of all refunds")
        void getAllRefund_Success() throws Exception {
            List<RefundDTO> refundList = List.of(mockRefundDTO);
            when(refundService.getAllRefunds()).thenReturn(refundList);

            ResponseEntity<List<RefundDTO>> response = refundController.getAllRefund();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());

            verify(refundService, times(1)).getAllRefunds();
        }

        @Test
        @DisplayName("Should return empty list when no refunds exist")
        void getAllRefund_Empty() throws Exception {
            when(refundService.getAllRefunds()).thenReturn(Collections.emptyList());

            ResponseEntity<List<RefundDTO>> response = refundController.getAllRefund();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());

            verify(refundService, times(1)).getAllRefunds();
        }
    }

    @Nested
    @DisplayName("GET /api/refunds/cashier/{cashierId} - getRefundByCashier")
    class GetRefundByCashierTests {

        @Test
        @DisplayName("Should return refunds associated with cashier ID")
        void getRefundByCashier_Success() throws Exception {
            Long cashierId = 10L;
            List<RefundDTO> refundList = List.of(mockRefundDTO);
            when(refundService.getRefundByCashier(cashierId)).thenReturn(refundList);

            ResponseEntity<List<RefundDTO>> response = refundController.getRefundByCashier(cashierId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());

            verify(refundService, times(1)).getRefundByCashier(cashierId);
        }
    }

    @Nested
    @DisplayName("GET /api/refunds/branch/{branchId} - getRefundByBranch")
    class GetRefundByBranchTests {

        @Test
        @DisplayName("Should return refunds associated with branch ID")
        void getRefundByBranch_Success() throws Exception {
            Long branchId = 5L;
            List<RefundDTO> refundList = List.of(mockRefundDTO);
            when(refundService.getRefundByBranch(branchId)).thenReturn(refundList);

            ResponseEntity<List<RefundDTO>> response = refundController.getRefundByBranch(branchId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());

            verify(refundService, times(1)).getRefundByBranch(branchId);
        }
    }

    @Nested
    @DisplayName("GET /api/refunds/shift/{shiftId} - getRefundByShift")
    class GetRefundByShiftTests {

        @Test
        @DisplayName("Should call getRefundByCashier with shift ID as implemented")
        void getRefundByShift_Success() throws Exception {
            Long shiftId = 15L;
            List<RefundDTO> refundList = List.of(mockRefundDTO);
            when(refundService.getRefundByCashier(shiftId)).thenReturn(refundList);

            ResponseEntity<List<RefundDTO>> response = refundController.getRefundByShift(shiftId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());

            verify(refundService, times(1)).getRefundByCashier(shiftId);
        }
    }

    @Nested
    @DisplayName("GET /api/refunds/cashier/{cashierId}/range - getRefundByCashierAndDateRange")
    class GetRefundByCashierAndDateRangeTests {

        @Test
        @DisplayName("Should return refunds for cashier within specified date-time range")
        void getRefundByCashierAndDateRange_Success() throws Exception {
            Long cashierId = 10L;
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();
            List<RefundDTO> refundList = List.of(mockRefundDTO);

            when(refundService.getRefundByCashierAndDateRange(cashierId, startDate, endDate))
                    .thenReturn(refundList);

            ResponseEntity<List<RefundDTO>> response = refundController.getRefundByCashierAndDateRange(
                    cashierId, startDate, endDate
            );

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());

            verify(refundService, times(1))
                    .getRefundByCashierAndDateRange(cashierId, startDate, endDate);
        }
    }

    @Nested
    @DisplayName("GET /api/refunds/{id} - getRefundById")
    class GetRefundByIdTests {

        @Test
        @DisplayName("Should return refund when valid ID is provided")
        void getRefundById_Success() throws Exception {
            Long refundId = 100L;
            when(refundService.getRefundById(refundId)).thenReturn(mockRefundDTO);

            ResponseEntity<RefundDTO> response = refundController.getRefundById(refundId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockRefundDTO, response.getBody());

            verify(refundService, times(1)).getRefundById(refundId);
        }

        @Test
        @DisplayName("Should throw exception when refund is not found")
        void getRefundById_NotFound() throws Exception {
            Long refundId = 999L;
            when(refundService.getRefundById(refundId)).thenThrow(new Exception("Refund not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    refundController.getRefundById(refundId)
            );

            assertEquals("Refund not found", exception.getMessage());
            verify(refundService, times(1)).getRefundById(refundId);
        }
    }
}