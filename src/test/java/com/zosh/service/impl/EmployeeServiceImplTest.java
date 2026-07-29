package com.zosh.service.impl;

import com.zosh.domain.UserRole;
import com.zosh.modal.Branch;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Store store;
    private Branch branch;
    private UserDto employeeDto;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setId(1L);
        branch = new Branch();
        branch.setId(1L);

        employeeDto = new UserDto();
        employeeDto.setEmail("emp@example.com");
        employeeDto.setPassword("plainPass");
        employeeDto.setFullName("Employee One");
    }

    @Test
    void createStoreEmployee_branchManager_success() throws Exception {
        employeeDto.setRole(UserRole.ROLE_BRANCH_MANAGER);
        employeeDto.setBranchID(1L);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(branchRepository.save(branch)).thenReturn(branch);

        UserDto result = employeeService.createStoreEmployee(employeeDto, 1L);

        assertNotNull(result);
        verify(branchRepository).save(branch);
    }

    @Test
    void createStoreEmployee_branchManagerMissingBranchId_throws() {
        employeeDto.setRole(UserRole.ROLE_BRANCH_MANAGER);
        employeeDto.setBranchID(null);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        assertThrows(Exception.class, () -> employeeService.createStoreEmployee(employeeDto, 1L));
    }

    @Test
    void createStoreEmployee_storeNotFound_throws() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.createStoreEmployee(employeeDto, 1L));
    }

    @Test
    void createStoreEmployee_nonManagerRole_success() throws Exception {
        employeeDto.setRole(UserRole.ROLE_STORE_MANAGER);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = employeeService.createStoreEmployee(employeeDto, 1L);

        assertNotNull(result);
        verify(branchRepository, never()).save(any());
    }

    @Test
    void createBranchEmployee_cashier_success() throws Exception {
        employeeDto.setRole(UserRole.ROLE_BRANCH_CASHIER);

        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = employeeService.createBranchEmployee(employeeDto, 1L);

        assertNotNull(result);
    }

    @Test
    void createBranchEmployee_unsupportedRole_throws() {
        employeeDto.setRole(UserRole.ROLE_STORE_ADMIN);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));

        assertThrows(Exception.class, () -> employeeService.createBranchEmployee(employeeDto, 1L));
    }

    @Test
    void createBranchEmployee_branchNotFound_throws() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.createBranchEmployee(employeeDto, 1L));
    }

    @Test
    void updateEmployee_success() throws Exception {
        User existing = new User();
        employeeDto.setRole(UserRole.ROLE_BRANCH_CASHIER);
        employeeDto.setBranchID(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(existing)).thenReturn(existing);

        User result = employeeService.updateEmployee(1L, employeeDto);

        assertEquals("emp@example.com", result.getEmail());
    }

    @Test
    void updateEmployee_employeeNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.updateEmployee(99L, employeeDto));
    }

    @Test
    void deteteEmployee_success() throws Exception {
        User existing = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        employeeService.deteteEmployee(1L);

        verify(userRepository).delete(existing);
    }

    @Test
    void deteteEmployee_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.deteteEmployee(99L));
    }

    @Test
    void findStoreEmployees_withRoleFilter_returnsFiltered() throws Exception {
        User cashier = new User();
        cashier.setRole(UserRole.ROLE_BRANCH_CASHIER);
        User manager = new User();
        manager.setRole(UserRole.ROLE_BRANCH_MANAGER);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(userRepository.findByStore(store)).thenReturn(Arrays.asList(cashier, manager));

        List<UserDto> result = employeeService.findStoreEmployees(1L, UserRole.ROLE_BRANCH_CASHIER);

        assertEquals(1, result.size());
    }

    @Test
    void findStoreEmployees_storeNotFound_throws() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.findStoreEmployees(99L, null));
    }

    @Test
    void findBranchEmployees_noRoleFilter_returnsAll() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(userRepository.findByBranchId(1L)).thenReturn(Arrays.asList(new User(), new User()));

        List<UserDto> result = employeeService.findBranchEmployees(1L, null);

        assertEquals(2, result.size());
    }

    @Test
    void findBranchEmployees_branchNotFound_throws() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> employeeService.findBranchEmployees(99L, null));
    }
}