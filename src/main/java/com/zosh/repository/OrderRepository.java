package com.zosh.repository;

import com.zosh.modal.Order;
import com.zosh.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByBranchId(Long branchID);
    List<Order> findByCashierId(Long cashierID);
    List<Order> findByBranchIdAndCreatedAtBetween(Long branchID, LocalDateTime from, LocalDateTime to);
    List<Order> findByCashierAndCreatedAtBetween(User cashier, LocalDateTime from, LocalDateTime to);
    List<Order> findTop5ByBranchIdOrderByCreatedAtDesc(Long branchID);
}
