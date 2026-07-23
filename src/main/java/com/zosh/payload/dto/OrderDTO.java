package com.zosh.payload.dto;

import com.zosh.domain.PaymentType;
import com.zosh.modal.Branch;
import com.zosh.modal.Customer;
import com.zosh.modal.OrderItem;
import com.zosh.modal.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private Long branchID;
    private Long customerID;
    private BranchDTO branch;
    private UserDto cashier;
    private Customer customer;
    private PaymentType paymentType;
    private List<OrderItemDTO> items;
}
