package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.domain.UserRole;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private UserDto managerDto;
    private UserDto cashierDto;
    private User userModal;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();

        // Sample Manager UserDto
        managerDto = new UserDto();
        managerDto.setId(20L);
        managerDto.setFullName("Narendra Mukkawar");
        managerDto.setEmail("narendraa.m@gmail.com");
        managerDto.setPhone("1122334455");
        managerDto.setPassword(null);
        managerDto.setBranchID(1L);
        managerDto.setStoreID(1L);
        managerDto.setRole(UserRole.ROLE_BRANCH_MANAGER);
        managerDto.setCreatedAt(null);
        managerDto.setUpdatedAt(null);
        managerDto.setLastLogin(null);

        // Sample Cashier UserDto
        cashierDto = new UserDto();
        cashierDto.setId(21L);
        cashierDto.setFullName("roj yadav");
        cashierDto.setEmail("roji@gmail.com");
        cashierDto.setPhone("25252525");
        cashierDto.setPassword(null);
        cashierDto.setBranchID(1L);
        cashierDto.setStoreID(null);
        cashierDto.setRole(UserRole.ROLE_BRANCH_CASHIER);
        cashierDto.setCreatedAt(null);
        cashierDto.setUpdatedAt(null);
        cashierDto.setLastLogin(null);

        // Sample User Modal for Update Endpoint
        userModal = new User();
        userModal.setId(20L);
        userModal.setFullName("Narendra Mukkawar");
        userModal.setEmail("narendraa.m@gmail.com");
        userModal.setPhone("1122334455");
    }

    @Nested
    @DisplayName("POST /api/employees/store/{storeID}")
    class CreateStoreEmployee {

        @Test
        @DisplayName("Should create store employee and verify response fields")
        void createStoreEmployee_Success() throws Exception {
            when(employeeService.createStoreEmployee(any(UserDto.class), eq(1L))).thenReturn(managerDto);

            mockMvc.perform(post("/api/employees/store/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(managerDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(20L))
                    .andExpect(jsonPath("$.fullName").value("Narendra Mukkawar"))
                    .andExpect(jsonPath("$.email").value("narendraa.m@gmail.com"))
                    .andExpect(jsonPath("$.phone").value("1122334455"))
                    .andExpect(jsonPath("$.password").value(managerDto.getPassword()))
                    .andExpect(jsonPath("$.branchID").value(1L))
                    .andExpect(jsonPath("$.storeID").value(1L))
                    .andExpect(jsonPath("$.role").value("ROLE_BRANCH_MANAGER"))
                    .andExpect(jsonPath("$.createdAt").value(managerDto.getCreatedAt()))
                    .andExpect(jsonPath("$.updatedAt").value(managerDto.getUpdatedAt()))
                    .andExpect(jsonPath("$.lastLogin").value(managerDto.getLastLogin()));

            verify(employeeService, times(1)).createStoreEmployee(any(UserDto.class), eq(1L));
        }

        @Test
        @DisplayName("Should throw Exception when store employee creation fails")
        void createStoreEmployee_ThrowsException() throws Exception {
            when(employeeService.createStoreEmployee(any(UserDto.class), eq(99L)))
                    .thenThrow(new Exception("Store not found with id 99"));

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/employees/store/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(managerDto)))
            );

            assertEquals("Store not found with id 99", exception.getCause().getMessage());
            verify(employeeService, times(1)).createStoreEmployee(any(UserDto.class), eq(99L));
        }
    }

    @Nested
    @DisplayName("POST /api/employees/branch/{branchID}")
    class CreateBranchEmployee {

        @Test
        @DisplayName("Should create branch employee (cashier) and verify response fields")
        void createBranchEmployee_Success() throws Exception {
            when(employeeService.createBranchEmployee(any(UserDto.class), eq(1L))).thenReturn(cashierDto);

            mockMvc.perform(post("/api/employees/branch/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cashierDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(21L))
                    .andExpect(jsonPath("$.fullName").value("roj yadav"))
                    .andExpect(jsonPath("$.email").value("roji@gmail.com"))
                    .andExpect(jsonPath("$.phone").value("25252525"))
                    .andExpect(jsonPath("$.password").value(cashierDto.getPassword()))
                    .andExpect(jsonPath("$.branchID").value(1L))
                    .andExpect(jsonPath("$.storeID").value(cashierDto.getStoreID()))
                    .andExpect(jsonPath("$.role").value("ROLE_BRANCH_CASHIER"))
                    .andExpect(jsonPath("$.createdAt").value(cashierDto.getCreatedAt()))
                    .andExpect(jsonPath("$.updatedAt").value(cashierDto.getUpdatedAt()))
                    .andExpect(jsonPath("$.lastLogin").value(cashierDto.getLastLogin()));

            verify(employeeService, times(1)).createBranchEmployee(any(UserDto.class), eq(1L));
        }
    }

    @Nested
    @DisplayName("PUT /api/employees/{id}")
    class UpdateEmployee {

        @Test
        @DisplayName("Should update employee details and return updated User")
        void updateEmployee_Success() throws Exception {
            when(employeeService.updateEmployee(eq(20L), any(UserDto.class))).thenReturn(userModal);

            mockMvc.perform(put("/api/employees/20")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(managerDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(20L))
                    .andExpect(jsonPath("$.fullName").value("Narendra Mukkawar"))
                    .andExpect(jsonPath("$.email").value("narendraa.m@gmail.com"))
                    .andExpect(jsonPath("$.phone").value("1122334455"));

            verify(employeeService, times(1)).updateEmployee(eq(20L), any(UserDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/employees/{id}")
    class DeleteEmployee {

        @Test
        @DisplayName("Should delete employee and return success message")
        void deleteEmployee_Success() throws Exception {
            doNothing().when(employeeService).deteteEmployee(20L);

            mockMvc.perform(delete("/api/employees/20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Employee deleted successfully"));

            verify(employeeService, times(1)).deteteEmployee(20L);
        }

        @Test
        @DisplayName("Should throw Exception when employee deletion fails")
        void deleteEmployee_NotFound_ThrowsException() throws Exception {
            doThrow(new Exception("Employee not found with id 99"))
                    .when(employeeService).deteteEmployee(99L);

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(delete("/api/employees/99"))
            );

            assertEquals("Employee not found with id 99", exception.getCause().getMessage());
            verify(employeeService, times(1)).deteteEmployee(99L);
        }
    }

    @Nested
    @DisplayName("GET /api/employees/store/{id}")
    class GetStoreEmployees {

        @Test
        @DisplayName("Should return store employees list when userRole parameter is null")
        void storeEmployee_WithoutUserRole_Success() throws Exception {
            List<UserDto> employees = List.of(managerDto, cashierDto);
            when(employeeService.findStoreEmployees(1L, null)).thenReturn(employees);

            mockMvc.perform(get("/api/employees/store/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(20L))
                    .andExpect(jsonPath("$[0].role").value("ROLE_BRANCH_MANAGER"))
                    .andExpect(jsonPath("$[1].id").value(21L))
                    .andExpect(jsonPath("$[1].role").value("ROLE_BRANCH_CASHIER"));

            verify(employeeService, times(1)).findStoreEmployees(1L, null);
        }

        @Test
        @DisplayName("Should return store employees filtered by userRole query param")
        void storeEmployee_WithUserRole_Success() throws Exception {
            List<UserDto> managers = List.of(managerDto);
            when(employeeService.findStoreEmployees(1L, UserRole.ROLE_BRANCH_MANAGER)).thenReturn(managers);

            mockMvc.perform(get("/api/employees/store/1")
                            .param("userRole", "ROLE_BRANCH_MANAGER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(20L))
                    .andExpect(jsonPath("$[0].role").value("ROLE_BRANCH_MANAGER"));

            verify(employeeService, times(1)).findStoreEmployees(1L, UserRole.ROLE_BRANCH_MANAGER);
        }
    }

    @Nested
    @DisplayName("GET /api/employees/branch/{id}")
    class GetBranchEmployees {

        @Test
        @DisplayName("Should return branch employees list")
        void branchEmployee_WithoutUserRole_Success() throws Exception {
            UserDto rahulCashier = new UserDto();
            rahulCashier.setId(15L);
            rahulCashier.setFullName("rahul");
            rahulCashier.setEmail("rahulgandhipappudon@gmail.com");
            rahulCashier.setPhone(null);
            rahulCashier.setPassword(null);
            rahulCashier.setBranchID(1L);
            rahulCashier.setStoreID(1L);
            rahulCashier.setRole(UserRole.ROLE_BRANCH_CASHIER);
            rahulCashier.setCreatedAt(null);
            rahulCashier.setUpdatedAt(null);
            rahulCashier.setLastLogin(null);

            List<UserDto> employees = List.of(managerDto, cashierDto, rahulCashier);
            when(employeeService.findBranchEmployees(1L, null)).thenReturn(employees);

            mockMvc.perform(get("/api/employees/branch/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))

                    // Element 0 (Manager)
                    .andExpect(jsonPath("$[0].id").value(20L))
                    .andExpect(jsonPath("$[0].fullName").value("Narendra Mukkawar"))

                    // Element 2 (Rahul Cashier)
                    .andExpect(jsonPath("$[2].id").value(15L))
                    .andExpect(jsonPath("$[2].fullName").value("rahul"))
                    .andExpect(jsonPath("$[2].email").value("rahulgandhipappudon@gmail.com"));

            verify(employeeService, times(1)).findBranchEmployees(1L, null);
        }

        @Test
        @DisplayName("Should return branch employees filtered by userRole query param")
        void branchEmployee_WithUserRole_Success() throws Exception {
            List<UserDto> cashiers = List.of(cashierDto);
            when(employeeService.findBranchEmployees(1L, UserRole.ROLE_BRANCH_CASHIER)).thenReturn(cashiers);

            mockMvc.perform(get("/api/employees/branch/1")
                            .param("userRole", "ROLE_BRANCH_CASHIER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(21L))
                    .andExpect(jsonPath("$[0].role").value("ROLE_BRANCH_CASHIER"));

            verify(employeeService, times(1)).findBranchEmployees(1L, UserRole.ROLE_BRANCH_CASHIER);
        }
    }
}