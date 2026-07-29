package com.zosh.controller;

import com.zosh.payload.dto.ShiftReportDTO;
import com.zosh.service.ShiftReportService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftReportControllerTest {

    @Mock
    private ShiftReportService shiftReportService;

    @InjectMocks
    private ShiftReportController shiftReportController;

    private ShiftReportDTO mockShiftReportDTO;

    @BeforeEach
    void setUp() {
        mockShiftReportDTO = new ShiftReportDTO();
    }

    @Nested
    @DisplayName("POST /api/shift-reports/start - startShift")
    class StartShiftTests {

        @Test
        @DisplayName("Should start shift successfully and return 200 OK")
        void startShift_Success() throws Exception {
            when(shiftReportService.startShift()).thenReturn(mockShiftReportDTO);

            ResponseEntity<ShiftReportDTO> response = shiftReportController.startShift();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockShiftReportDTO, response.getBody());

            verify(shiftReportService, times(1)).startShift();
        }

        @Test
        @DisplayName("Should propagate exception when shift start fails")
        void startShift_Exception() throws Exception {
            when(shiftReportService.startShift()).thenThrow(new RuntimeException("Shift already active"));

            Exception exception = assertThrows(RuntimeException.class, () ->
                    shiftReportController.startShift()
            );

            assertEquals("Shift already active", exception.getMessage());
            verify(shiftReportService, times(1)).startShift();
        }
    }

    @Nested
    @DisplayName("PATCH /api/shift-reports/end - endShift")
    class EndShiftTests {

        @Test
        @DisplayName("Should end shift with null parameters as defined in controller")
        void endShift_Success() throws Exception {
            when(shiftReportService.endShift(null, null)).thenReturn(mockShiftReportDTO);

            ResponseEntity<ShiftReportDTO> response = shiftReportController.endShift();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockShiftReportDTO, response.getBody());

            verify(shiftReportService, times(1)).endShift(null, null);
        }
    }

    @Nested
    @DisplayName("GET /api/shift-reports/current - getCurrentShiftProgress")
    class GetCurrentShiftProgressTests {

        @Test
        @DisplayName("Should return current shift progress with null parameter as defined")
        void getCurrentShiftProgress_Success() throws Exception {
            when(shiftReportService.getCurrentShiftProgress(null)).thenReturn(mockShiftReportDTO);

            ResponseEntity<ShiftReportDTO> response = shiftReportController.getCurrentShiftProgress();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockShiftReportDTO, response.getBody());

            verify(shiftReportService, times(1)).getCurrentShiftProgress(null);
        }
    }

    @Nested
    @DisplayName("GET /api/shift-reports/cashier/{cashierId}/by-date - getShiftReportByDate")
    class GetShiftReportByDateTests {

        @Test
        @DisplayName("Should return shift report for cashier on specific date-time")
        void getShiftReportByDate_Success() throws Exception {
            Long cashierId = 10L;
            LocalDateTime dateTime = LocalDateTime.now();

            when(shiftReportService.getShiftByCashierAndDate(cashierId, dateTime)).thenReturn(mockShiftReportDTO);

            ResponseEntity<ShiftReportDTO> response = shiftReportController.getShiftReportByDate(cashierId, dateTime);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockShiftReportDTO, response.getBody());

            verify(shiftReportService, times(1)).getShiftByCashierAndDate(cashierId, dateTime);
        }
    }

    @Nested
    @DisplayName("GET /api/shift-reports/cashier/{cashierId} - getShiftByCashier")
    class GetShiftByCashierTests {

        @Test
        @DisplayName("Should return list of shift reports for cashier")
        void getShiftByCashier_Success() throws Exception {
            Long cashierId = 10L;
            List<ShiftReportDTO> reports = List.of(mockShiftReportDTO);

            when(shiftReportService.getShiftReportByCashierId(cashierId)).thenReturn(reports);

            ResponseEntity<List<ShiftReportDTO>> response = shiftReportController.getShiftByCashier(cashierId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(shiftReportService, times(1)).getShiftReportByCashierId(cashierId);
        }

        @Test
        @DisplayName("Should return empty list when no shift reports found for cashier")
        void getShiftByCashier_Empty() throws Exception {
            Long cashierId = 10L;
            when(shiftReportService.getShiftReportByCashierId(cashierId)).thenReturn(Collections.emptyList());

            ResponseEntity<List<ShiftReportDTO>> response = shiftReportController.getShiftByCashier(cashierId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());

            verify(shiftReportService, times(1)).getShiftReportByCashierId(cashierId);
        }
    }

    @Nested
    @DisplayName("GET /api/shift-reports/branch/{branchId} - getShiftByBranch")
    class GetShiftByBranchTests {

        @Test
        @DisplayName("Should return list of shift reports for branch")
        void getShiftByBranch_Success() throws Exception {
            Long branchId = 5L;
            List<ShiftReportDTO> reports = List.of(mockShiftReportDTO);

            when(shiftReportService.getShiftReportByBranchId(branchId)).thenReturn(reports);

            ResponseEntity<List<ShiftReportDTO>> response = shiftReportController.getShiftByBranch(branchId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(shiftReportService, times(1)).getShiftReportByBranchId(branchId);
        }
    }

    @Nested
    @DisplayName("GET /api/shift-reports/{Id} - getShiftById")
    class GetShiftByIdTests {

        @Test
        @DisplayName("Should return shift report by ID")
        void getShiftById_Success() throws Exception {
            Long shiftId = 100L;
            when(shiftReportService.getShiftReportById(shiftId)).thenReturn(mockShiftReportDTO);

            ResponseEntity<ShiftReportDTO> response = shiftReportController.getShiftById(shiftId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockShiftReportDTO, response.getBody());

            verify(shiftReportService, times(1)).getShiftReportById(shiftId);
        }

        @Test
        @DisplayName("Should throw exception when shift report is not found")
        void getShiftById_NotFound() throws Exception {
            Long shiftId = 999L;
            when(shiftReportService.getShiftReportById(shiftId)).thenThrow(new Exception("Shift report not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    shiftReportController.getShiftById(shiftId)
            );

            assertEquals("Shift report not found", exception.getMessage());
            verify(shiftReportService, times(1)).getShiftReportById(shiftId);
        }
    }
}