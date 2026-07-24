package com.zosh.service;

import com.zosh.exceptions.UserException;
import com.zosh.modal.ShiftReport;
import com.zosh.payload.dto.ShiftReportDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftReportService {

    ShiftReportDTO startShift(/*Long cashierId, Long branchId, LocalDateTime shiftStart*/) throws Exception;
    ShiftReportDTO endShift(Long shiftReportId, LocalDateTime shiftEnd) throws Exception;
    ShiftReportDTO getShiftReportById(Long shiftReportId) throws Exception;
    List<ShiftReportDTO> getAllShiftReport() throws Exception;
    List<ShiftReportDTO> getShiftReportByBranchId(Long branchId) throws Exception;
    List<ShiftReportDTO> getShiftReportByCashierId(Long cashierId) throws Exception;
    ShiftReportDTO getCurrentShiftProgress(Long cashierId) throws Exception;
    ShiftReportDTO getShiftByCashierAndDate(Long cashierId, LocalDateTime date) throws Exception;
}
