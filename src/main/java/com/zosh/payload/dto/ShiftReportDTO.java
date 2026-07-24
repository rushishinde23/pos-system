package com.zosh.payload.dto;

import com.zosh.modal.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftReportDTO {

    private Long id;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    private Double totalSales;
    private Double totalRefunds;
    private Double netSale;
    private int totalOrders;
    private UserDto cashier;
    private BranchDTO branch;
    private List<PaymentSummary> paymentSummaries;
    private List<ProductDTO> topSellingProducts;
    private List<OrderDTO> recentOrders;
    private List<RefundDTO> refunds;
    private Long branchId;
    private Long cashierId;
}
