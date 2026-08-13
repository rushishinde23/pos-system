package com.zosh.service;

import com.zosh.exceptions.UserException;
import com.zosh.modal.Branch;
import com.zosh.modal.User;
import com.zosh.payload.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    BranchDTO createBranch(BranchDTO branchDTO) throws UserException;
    BranchDTO updateBranch(Long id, BranchDTO branchDTO) throws Exception;
    void deleteBranch(Long id) throws Exception;
    List<BranchDTO> getAllBranchesByStoreID(Long storeID);
    BranchDTO getBranchByID(Long id) throws Exception;
}
