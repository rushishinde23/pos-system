package com.zosh.mapper;

import com.zosh.modal.Refund;
import com.zosh.payload.dto.RefundDTO;

public class RefundMapper {

    public static RefundDTO toDTO(Refund refund){
        return RefundDTO.builder()
                .id(refund.getId())
                .orderId(refund.getOrder().getId())
                .reason(refund.getReason())
                .amount(refund.getAmount())
                .cashierName(refund.getCashier().getFullName())
                .branchId(refund.getBranch().getId())
                .shiftReportId(refund.getShiftReport()!=null?
                        refund.getShiftReport().getId():null)
                .createdAt(refund.getCreatedAt())
                .build();
    }

}
