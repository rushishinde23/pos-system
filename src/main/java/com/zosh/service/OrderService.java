package com.zosh.service;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import com.zosh.payload.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO) throws Exception;

    OrderDTO getOrderById(Long orderID)throws Exception;

    List<OrderDTO> getOrdersByBranch(Long branchId, Long customerId, Long cashierId, PaymentType paymentType,
                                     OrderStatus orderStatus) throws Exception;

    List<OrderDTO> getOrderByCashier(Long cashierId);
    void deleteOrder(Long orderId) throws Exception;

    List<OrderDTO> getTodayOrdersByBranch(Long branchId) throws Exception;

    List<OrderDTO> getOrdersByCustomerId(Long customerId) throws Exception;

    List<OrderDTO> getTop5RecentOrdesBranchId(Long branchId) throws Exception;
}
