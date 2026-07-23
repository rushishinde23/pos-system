package com.zosh.service.impl;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import com.zosh.mapper.OrderMapper;
import com.zosh.modal.*;
import com.zosh.payload.dto.OrderDTO;
import com.zosh.repository.OrderItemRepository;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.service.OrderService;
import com.zosh.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) throws Exception {
        User cashier = userService.getCurrentUser();
        Branch branch = cashier.getBranch();
        if(branch==null){
            throw new Exception("Cashier's branch not found");
        }
        Order order = Order.builder()
                .branch(branch)
                .cashier(cashier)
                .customer(orderDTO.getCustomer())
                .paymentType(orderDTO.getPaymentType())
                .build();

        List<OrderItem> orderedItems =  orderDTO.getItems().stream().map(
                orderItemDTO -> {
                    Product product = productRepository.findById(orderItemDTO.getProductId())
                            .orElseThrow(()->new EntityNotFoundException("Product not found"));

                    return OrderItem.builder().product(product)
                            .quantity(orderItemDTO.getQuantity())
                            .price(product.getSellingPrice()*orderItemDTO.getQuantity())
                            .order(order)
                            .build();
                }
        ).toList();
        double total = orderedItems.stream().mapToDouble(
                OrderItem::getPrice
        ).sum();
        order.setTotalAmount(total);
        order.setItems(orderedItems);

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long orderID) throws Exception {
        return orderRepository.findById(orderID).map(OrderMapper::toDTO).orElseThrow(
                ()->new Exception("Order not found with id "+ orderID)
        );
    }

    @Override
    public List<OrderDTO> getOrdersByBranch(Long branchId,
                                            Long customerId,
                                            Long cashierId,
                                            PaymentType paymentType,
                                            OrderStatus orderStatus) throws Exception {
        return orderRepository.findByBranchId(branchId).stream()
                .filter(order -> customerId==null || (order.getCustomer()!=null && order.getCustomer().getId().equals(customerId)))
                .filter(order -> cashierId==null || (order.getCashier()!=null && order.getCashier().getId().equals(cashierId)))
                .filter(order -> paymentType==null || order.getPaymentType()==paymentType)
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrderByCashier(Long cashierId) {
        return orderRepository.findByCashierId(cashierId).stream().map(OrderMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ()->new Exception("Order not found with id "+ orderId)
        );
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDTO> getTodayOrdersByBranch(Long branchId) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return orderRepository.findByBranchIdAndCreatedAtBetween(branchId, start, end).stream()
                .map(OrderMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) throws Exception {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getTop5RecentOrdesBranchId(Long branchId) throws Exception {
        return orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(branchId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
}
