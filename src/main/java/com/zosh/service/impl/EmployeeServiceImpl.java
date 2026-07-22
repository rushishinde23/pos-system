package com.zosh.service.impl;

import com.zosh.domain.UserRole;
import com.zosh.mapper.UserMapper;
import com.zosh.modal.Branch;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.repository.UserRepository;
import com.zosh.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createStoreEmployee(UserDto employee, Long storeID) throws Exception {
        Store store = storeRepository.findById(storeID).orElseThrow(
                ()->new Exception("Store not found")
        );
        Branch branch = null;
        if(employee.getRole()==UserRole.ROLE_BRANCH_MANAGER){
            if(employee.getBranchID()==null){
                throw new Exception("Branch id is required to create branch manager");
            }
            branch = branchRepository.findById(employee.getBranchID()).orElseThrow(
                    ()-> new Exception("Branch not found")
            );
        }
        User user = UserMapper.toEntity(employee);
        user.setStore(store);
        user.setBranch(branch);
        user.setPassword(passwordEncoder.encode(employee.getPassword()));
        User savedEmployee = userRepository.save(user);
        if(employee.getRole()==UserRole.ROLE_BRANCH_MANAGER && branch != null){
            branch.setManager(savedEmployee);
            branchRepository.save(branch);
        }
        return UserMapper.toDTO(savedEmployee);
    }

    @Override
    public UserDto createBranchEmployee(UserDto employee, Long branchID) throws Exception {

        Branch branch = branchRepository.findById(branchID).orElseThrow(
                ()-> new Exception("Branch not found")
        );
        if(employee.getRole()==UserRole.ROLE_BRANCH_CASHIER || employee.getRole()==UserRole.ROLE_BRANCH_MANAGER){
            User user = UserMapper.toEntity(employee);
            user.setBranch(branch);
            user.setPassword(passwordEncoder.encode(employee.getPassword()));
            return UserMapper.toDTO(userRepository.save(user));
        }
        throw new Exception("Branch role not supported");
    }

    @Override
    public User updateEmployee(Long employeeID, UserDto employeeDetails) throws Exception {

        User existingEmployee = userRepository.findById(employeeID).orElseThrow(
                ()->new Exception("Employee does not exist with provided id")
        );
        Branch branch = branchRepository.findById(employeeDetails.getBranchID()).orElseThrow(
                ()->new Exception("Branch not found")
        );
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setFullName(employeeDetails.getFullName());
        existingEmployee.setPassword(passwordEncoder.encode(employeeDetails.getPassword()));
        existingEmployee.setRole(employeeDetails.getRole());
        existingEmployee.setBranch(branch);
        existingEmployee.setPhone(employeeDetails.getPhone());
        return userRepository.save(existingEmployee);
    }

    @Override
    public void deteteEmployee(Long employeeID) throws Exception {
        User existingEmployee = userRepository.findById(employeeID).orElseThrow(
                ()->new Exception("Employee does not exist with provided id")
        );
        userRepository.delete(existingEmployee);
    }

    @Override
    public List<UserDto> findStoreEmployees(Long storeID, UserRole role) throws Exception {
        Store store = storeRepository.findById(storeID).orElseThrow(
                ()->new Exception("Store not found")
        );
        return userRepository.findByStore(store).stream()
                .filter(user -> role==null || user.getRole()==role)
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findBranchEmployees(Long branchID, UserRole role) throws Exception {
        Branch branch = branchRepository.findById(branchID).orElseThrow(
                ()->new Exception("Branch not found")
        );

        return userRepository.findByBranchId(branchID).stream()
                .filter(user->role==null || user.getRole()==role)
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
