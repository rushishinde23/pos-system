package com.zosh.controller;

import com.zosh.domain.UserRole;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/store/{storeID}")
    public ResponseEntity<UserDto> createStoreEmployee(@RequestBody UserDto userDto, @PathVariable Long storeID) throws Exception {
        UserDto employee = employeeService.createStoreEmployee(userDto, storeID);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/branch/{branchID}")
    public ResponseEntity<UserDto> createBranchEmployee(@RequestBody UserDto userDto, @PathVariable Long branchID) throws Exception {
        UserDto employee = employeeService.createBranchEmployee(userDto, branchID);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateEmployee(@RequestBody UserDto userDto, @PathVariable Long id) throws Exception {
        User employee = employeeService.updateEmployee(id, userDto);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) throws Exception {
        employeeService.deteteEmployee(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Employee deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/store/{id}")
    public ResponseEntity<List<UserDto>> storeEmployee(@PathVariable Long id, @RequestParam(required = false)UserRole userRole) throws Exception {
        List<UserDto> employee = employeeService.findStoreEmployees(id, userRole);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/branch/{id}")
    public ResponseEntity<List<UserDto>> branchEmployee(@PathVariable Long id, @RequestParam(required = false)UserRole userRole) throws Exception {
        List<UserDto> employee = employeeService.findBranchEmployees(id, userRole);
        return ResponseEntity.ok(employee);
    }
}
