package com.zosh.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zosh.domain.PaymentType;
import com.zosh.modal.Branch;
import com.zosh.modal.Order;
import com.zosh.modal.ShiftReport;
import com.zosh.modal.User;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    private  Long id;


    private OrderDTO order;

    private Long orderId;

    private String reason;

    private Double amount;

//    private ShiftReport shiftReport;

    private Long shiftReportId;

    private User cashier;

    private String cashierName;

    private Branch branch;

    private Long branchId;

    private LocalDateTime createdAt;

    private PaymentType paymentType;

}
