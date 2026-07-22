package com.zosh.service;

import com.zosh.domain.UserRole;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;

import java.util.List;

public interface EmployeeService {

    UserDto createStoreEmployee(UserDto employee, Long storeID) throws Exception;
    UserDto createBranchEmployee(UserDto employee, Long branchID) throws Exception;
    User updateEmployee(Long employeeID, UserDto employeeDetails) throws Exception;
    void deteteEmployee(Long employeeID) throws Exception;
    List<User> findStoreEmployees(Long storeID, UserRole role) throws Exception;
    List<User> findBranchEmployees(Long branchID, UserRole role) throws Exception;
}
