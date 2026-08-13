/*
package com.zosh.service.impl;

import com.zosh.domain.PaymentType;
import com.zosh.exceptions.UserException;
import com.zosh.modal.*;
import com.zosh.payload.dto.ShiftReportDTO;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.RefundRepository;
import com.zosh.repository.ShiftReportRepository;
import com.zosh.repository.UserRepository;
import com.zosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftReportServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ShiftReportRepository shiftReportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private ShiftReportServiceImpl shiftReportService;

    private User cashier;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(1L);
        cashier = new User();
        cashier.setId(1L);
        cashier.setBranch(branch);
    }

    @Test
    void startShift_success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findByCashierAndShiftStartBetween(eq(cashier), any(), any()))
                .thenReturn(Optional.empty());
        when(shiftReportRepository.save(any(ShiftReport.class))).thenAnswer(inv -> inv.getArgument(0));

        ShiftReportDTO result = shiftReportService.startShift();

        assertNotNull(result);
    }

    @Test
    void startShift_alreadyStarted_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findByCashierAndShiftStartBetween(eq(cashier), any(), any()))
                .thenReturn(Optional.of(new ShiftReport()));

        assertThrows(Exception.class, () -> shiftReportService.startShift());
    }

    @Test
    void endShift_success_computesTotals() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        ShiftReport activeShift = ShiftReport.builder()
                .cashier(cashier).shiftStart(start).branch(branch).build();

        Product product = new Product();
        product.setId(1L);
        product.setCategory(Category.builder().name("Snacks").build());
        OrderItem item = OrderItem.builder().product(product).quantity(2).price(20.0).build();
        Order order = Order.builder().totalAmount(20.0).paymentType(PaymentType.CASH).branch(branch).build();
        order.setItems(List.of(item));
        order.setCreatedAt(LocalDateTime.now());

        Refund refund = Refund.builder().amount(5.0).build();

        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier))
                .thenReturn(Optional.of(activeShift));
        when(refundRepository.findByCashierIdAndCreatedAtBetween(eq(1L), any(), any()))
                .thenReturn(List.of(refund));
        when(orderRepository.findByCashierAndCreatedAtBetween(eq(cashier), any(), any()))
                .thenReturn(List.of(order));
        when(shiftReportRepository.save(activeShift)).thenReturn(activeShift);

        ShiftReportDTO result = shiftReportService.endShift(1L, LocalDateTime.now());

        assertNotNull(result);
        assertEquals(20.0, activeShift.getTotalSales());
        assertEquals(5.0, activeShift.getTotalRefunds());
        assertEquals(15.0, activeShift.getNetSale());
        assertEquals(1, activeShift.getTotalOrders());
    }

    @Test
    void endShift_noActiveShift_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> shiftReportService.endShift(1L, LocalDateTime.now()));
    }

    @Test
    void getShiftReportById_success() throws Exception {
        ShiftReport report = ShiftReport.builder().cashier(cashier).branch(branch).build();
        when(shiftReportRepository.findById(1L)).thenReturn(Optional.of(report));

        ShiftReportDTO result = shiftReportService.getShiftReportById(1L);

        assertNotNull(result);
    }

    @Test
    void getShiftReportById_notFound_throws() {
        when(shiftReportRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> shiftReportService.getShiftReportById(99L));
    }

    @Test
    void getAllShiftReport_returnsList() throws Exception {
        ShiftReport report = ShiftReport.builder().cashier(cashier).branch(branch).build();
        when(shiftReportRepository.findAll()).thenReturn(Arrays.asList(report, report));
        assertEquals(2, shiftReportService.getAllShiftReport().size());
    }

    @Test
    void getShiftReportByBranchId_returnsList() throws Exception {
        ShiftReport report = ShiftReport.builder().cashier(cashier).branch(branch).build();
        when(shiftReportRepository.findByBranchId(1L)).thenReturn(List.of(report));
        assertEquals(1, shiftReportService.getShiftReportByBranchId(1L).size());
    }

    @Test
    void getShiftReportByCashierId_returnsList() throws Exception {
        ShiftReport report = ShiftReport.builder().cashier(cashier).branch(branch).build();
        when(shiftReportRepository.findByCashierId(1L)).thenReturn(List.of(report));
        assertEquals(1, shiftReportService.getShiftReportByCashierId(1L).size());
    }

    @Test
    void getCurrentShiftProgress_success() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        ShiftReport activeShift = ShiftReport.builder()
                .cashier(cashier).shiftStart(start).branch(branch).build();

        Order order = Order.builder().totalAmount(30.0).paymentType(PaymentType.CARD).branch(branch).build();
        order.setItems(List.of());
        order.setCreatedAt(LocalDateTime.now());

        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier))
                .thenReturn(Optional.of(activeShift));
        when(orderRepository.findByCashierAndCreatedAtBetween(eq(cashier), any(), any()))
                .thenReturn(List.of(order));
        when(refundRepository.findByCashierIdAndCreatedAtBetween(eq(1L), any(), any()))
                .thenReturn(List.of());
        when(shiftReportRepository.save(activeShift)).thenReturn(activeShift);

        ShiftReportDTO result = shiftReportService.getCurrentShiftProgress(1L);

        assertNotNull(result);
        assertEquals(30.0, activeShift.getTotalSales());
    }

    @Test
    void getCurrentShiftProgress_noActiveShift_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(cashier);
        when(shiftReportRepository.findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> shiftReportService.getCurrentShiftProgress(1L));
    }

    @Test
    void getShiftByCashierAndDate_success() throws Exception {
        ShiftReport report = ShiftReport.builder().cashier(cashier).branch(branch).build();
        LocalDateTime date = LocalDateTime.now();

        when(userRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(shiftReportRepository.findByCashierAndShiftStartBetween(eq(cashier), any(), any()))
                .thenReturn(Optional.of(report));

        ShiftReportDTO result = shiftReportService.getShiftByCashierAndDate(1L, date);

        assertNotNull(result);
    }

    @Test
    void getShiftByCashierAndDate_cashierNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class,
                () -> shiftReportService.getShiftByCashierAndDate(99L, LocalDateTime.now()));
    }

    @Test
    void getShiftByCashierAndDate_reportNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(shiftReportRepository.findByCashierAndShiftStartBetween(eq(cashier), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> shiftReportService.getShiftByCashierAndDate(1L, LocalDateTime.now()));
    }
}*/
package com.zosh.service.impl;

import com.zosh.mapper.ShiftReportMapper;
import com.zosh.modal.*;
import com.zosh.repository.*;
import com.zosh.service.UserService;
import com.zosh.payload.dto.ShiftReportDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ShiftReportServiceImplTest {


    @Mock
    private UserService userService;

    @Mock
    private ShiftReportRepository shiftReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RefundRepository refundRepository;


    @InjectMocks
    private ShiftReportServiceImpl shiftReportService;


    private User cashier;
    private Branch branch;
    private ShiftReport shiftReport;


    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);


        branch = Branch.builder()
                .id(1L)
                .build();


        cashier = User.builder()
                .id(10L)
                .branch(branch)
                .build();


        shiftReport = ShiftReport.builder()
                .id(100L)
                .cashier(cashier)
                .branch(branch)
                .shiftStart(LocalDateTime.now())
                .build();

    }



    // ================= START SHIFT ===================


    @Test
    void shouldStartShiftSuccessfully() throws Exception {


        when(userService.getCurrentUser())
                .thenReturn(cashier);


        when(
                shiftReportRepository.findByCashierAndShiftStartBetween(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(Optional.empty());


        when(shiftReportRepository.save(any()))
                .thenReturn(shiftReport);



        ShiftReportDTO result =
                shiftReportService.startShift();



        assertNotNull(result);

        verify(shiftReportRepository)
                .save(any(ShiftReport.class));

    }




    @Test
    void shouldNotStartDuplicateShift() throws Exception {


        when(userService.getCurrentUser())
                .thenReturn(cashier);


        when(
                shiftReportRepository.findByCashierAndShiftStartBetween(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(Optional.of(shiftReport));



        Exception exception =
                assertThrows(
                        Exception.class,
                        ()->shiftReportService.startShift()
                );


        assertEquals(
                "Shift already started today",
                exception.getMessage()
        );

    }





    // ================= END SHIFT ===================


    @Test
    void shouldEndShiftSuccessfully() throws Exception {


        LocalDateTime end =
                LocalDateTime.now();


        when(userService.getCurrentUser())
                .thenReturn(cashier);



        when(
                shiftReportRepository
                        .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier)
        )
                .thenReturn(Optional.of(shiftReport));



        when(
                orderRepository.findByCashierAndCreatedAtBetween(
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(new ArrayList<>());



        when(
                refundRepository.findByCashierIdAndCreatedAtBetween(
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(new ArrayList<>());



        when(shiftReportRepository.save(any()))
                .thenReturn(shiftReport);



        ShiftReportDTO result =
                shiftReportService.endShift(
                        100L,
                        end
                );


        assertNotNull(result);


        verify(shiftReportRepository)
                .save(any());

    }





    @Test
    void shouldThrowExceptionWhenNoActiveShift() throws Exception {


        when(userService.getCurrentUser())
                .thenReturn(cashier);


        when(
                shiftReportRepository
                        .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier)
        )
                .thenReturn(Optional.empty());



        Exception exception =
                assertThrows(
                        Exception.class,
                        () ->
                                shiftReportService.endShift(
                                        1L,
                                        LocalDateTime.now()
                                )
                );


        assertEquals(
                "Shift not found",
                exception.getMessage()
        );

    }





    // =============== GET BY ID ===================


    @Test
    void shouldGetShiftReportById() throws Exception {


        when(
                shiftReportRepository.findById(100L)
        )
                .thenReturn(Optional.of(shiftReport));



        ShiftReportDTO result =
                shiftReportService.getShiftReportById(100L);



        assertNotNull(result);

        assertEquals(
                100L,
                result.getId()
        );

    }





    @Test
    void shouldThrowExceptionWhenShiftIdNotFound(){


        when(
                shiftReportRepository.findById(99L)
        )
                .thenReturn(Optional.empty());



        Exception exception =
                assertThrows(
                        Exception.class,
                        () ->
                                shiftReportService
                                        .getShiftReportById(99L)
                );



        assertEquals(
                "Shift not found",
                exception.getMessage()
        );

    }




    // =============== GET ALL ===================


    @Test
    void shouldGetAllShiftReports() throws Exception {


        when(
                shiftReportRepository.findAll()
        )
                .thenReturn(
                        List.of(shiftReport)
                );



        List<ShiftReportDTO> result =
                shiftReportService.getAllShiftReport();



        assertEquals(
                1,
                result.size()
        );

    }




    // =============== BY BRANCH ===================


    @Test
    void shouldGetShiftByBranchId() throws Exception {


        when(
                shiftReportRepository.findByBranchId(1L)
        )
                .thenReturn(
                        List.of(shiftReport)
                );



        List<ShiftReportDTO> result =
                shiftReportService.getShiftReportByBranchId(1L);



        assertFalse(result.isEmpty());

    }




    // =============== BY CASHIER ===================


    @Test
    void shouldGetShiftByCashierId() throws Exception {


        when(
                shiftReportRepository.findByCashierId(10L)
        )
                .thenReturn(
                        List.of(shiftReport)
                );



        List<ShiftReportDTO> result =
                shiftReportService.getShiftReportByCashierId(10L);



        assertEquals(
                1,
                result.size()
        );

    }





    // =============== CURRENT SHIFT ===================


    @Test
    void shouldGetCurrentShiftProgress() throws Exception {


        when(userService.getCurrentUser())
                .thenReturn(cashier);



        when(
                shiftReportRepository
                        .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier)
        )
                .thenReturn(Optional.of(shiftReport));



        when(
                orderRepository.findByCashierAndCreatedAtBetween(
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(new ArrayList<>());



        when(
                refundRepository.findByCashierIdAndCreatedAtBetween(
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(new ArrayList<>());



        when(shiftReportRepository.save(any()))
                .thenReturn(shiftReport);



        ShiftReportDTO result =
                shiftReportService.getCurrentShiftProgress(10L);



        assertNotNull(result);

    }





    // =============== DATE SEARCH ===================


    @Test
    void shouldGetShiftByCashierAndDate() throws Exception {


        when(
                userRepository.findById(10L)
        )
                .thenReturn(Optional.of(cashier));



        when(
                shiftReportRepository
                        .findByCashierAndShiftStartBetween(
                                any(),
                                any(),
                                any()
                        ))
                .thenReturn(Optional.of(shiftReport));



        ShiftReportDTO result =
                shiftReportService.getShiftByCashierAndDate(
                        10L,
                        LocalDateTime.now()
                );



        assertNotNull(result);

    }



}
