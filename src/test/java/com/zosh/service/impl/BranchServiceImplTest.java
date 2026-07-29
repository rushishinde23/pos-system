package com.zosh.service.impl;

import com.zosh.modal.Branch;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.BranchDTO;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private BranchServiceImpl branchService;

    private User currentUser;
    private Store store;
    private BranchDTO branchDTO;
    private Branch branch;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);

        store = new Store();

        branchDTO = new BranchDTO();
        branchDTO.setName("Downtown");
        branchDTO.setEmail("branch@example.com");
        branchDTO.setPnone("9999999999");
        branchDTO.setAddress("123 Main St");
        branchDTO.setOpenTime(LocalTime.of(9, 0));
        branchDTO.setCloseTime(LocalTime.of(21, 0));

        branch = new Branch();
        branch.setId(1L);
        branch.setName("Downtown");
    }

    @Test
    void createBranch_success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(storeRepository.findByStoreAdminId(1L)).thenReturn(store);
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        BranchDTO result = branchService.createBranch(branchDTO);

        assertNotNull(result);
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void updateBranch_success_updatesFields() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchRepository.save(branch)).thenReturn(branch);

        BranchDTO result = branchService.updateBranch(1L, branchDTO);

        assertNotNull(result);
        assertEquals("Downtown", branch.getName());
    }

    @Test
    void updateBranch_notFound_throws() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> branchService.updateBranch(99L, branchDTO));
    }

    @Test
    void deleteBranch_success() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        branchService.deleteBranch(1L);
        verify(branchRepository).delete(branch);
    }

    @Test
    void deleteBranch_notFound_throws() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> branchService.deleteBranch(99L));
    }

    @Test
    void getAllBranchesByStoreID_returnsList() {
        when(branchRepository.findByStoreId(1L)).thenReturn(Arrays.asList(branch, new Branch()));
        List<BranchDTO> result = branchService.getAllBranchesByStoreID(1L);
        assertEquals(2, result.size());
    }

    @Test
    void getBranchByID_success() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        BranchDTO result = branchService.getBranchByID(1L);
        assertNotNull(result);
    }

    @Test
    void getBranchByID_notFound_throws() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> branchService.getBranchByID(99L));
    }
}