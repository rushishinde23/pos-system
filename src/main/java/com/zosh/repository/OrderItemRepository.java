package com.zosh.repository;

import com.zosh.modal.OrderItem;
import com.zosh.payload.dto.OrderItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
