package com.zosh.mapper;

import com.zosh.modal.Order;
import com.zosh.modal.Product;
import com.zosh.modal.Refund;
import com.zosh.modal.ShiftReport;
import com.zosh.payload.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class ShiftReportMapper {

    public static ShiftReportDTO toDTO(ShiftReport shiftReport){
        return ShiftReportDTO.builder()
                .id(shiftReport.getId())
                .shiftStart(shiftReport.getShiftStart())
                .shiftEnd((shiftReport.getShiftEnd()))
                .totalSales(shiftReport.getTotalSales())
                .totalOrders(shiftReport.getTotalOrders())
                .netSale(shiftReport.getNetSale())
                .totalRefunds(shiftReport.getTotalRefunds())
                .cashier(UserMapper.toDTO(shiftReport.getCashier()))
                .cashierId(shiftReport.getCashier().getId())
                .branchId(shiftReport.getBranch().getId())
                .recentOrders(mapOrders(shiftReport.getRecentOrders()))
                .topSellingProducts(mapProducts(shiftReport.getTopSellingProducts()))
                .refunds(mapRefunds(shiftReport.getRefunds()))
                .paymentSummaries(shiftReport.getPaymentSummaries())
                .build();
    }

    private static List<RefundDTO> mapRefunds(List<Refund> refunds) {
        if(refunds == null || refunds.isEmpty()) return null;

        return refunds.stream().map(RefundMapper::toDTO).collect(Collectors.toList());
    }

    private static List<ProductDTO> mapProducts(List<Product> topSellingProducts) {
        if(topSellingProducts == null || topSellingProducts.isEmpty()) return null;

        return topSellingProducts.stream().map(ProductMapper::toDTO).collect(Collectors.toList());
    }

    private static List<OrderDTO> mapOrders(List<Order> recentOrders) {
        if(recentOrders == null || recentOrders.isEmpty()) return null;

        return recentOrders.stream().map(OrderMapper::toDTO).collect(Collectors.toList());
    }
}
