package com.zosh.controller;

import com.zosh.exceptions.UserException;
import com.zosh.modal.Branch;
import com.zosh.payload.dto.BranchDTO;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.BranchService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;


    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchDTO branchDTO) throws UserException {
        BranchDTO createdBranch = branchService.createBranch(branchDTO);
        return ResponseEntity.ok(createdBranch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Long id) throws Exception {
        BranchDTO getBranch = branchService.getBranchByID(id);
        return ResponseEntity.ok(getBranch);
    }

    @GetMapping("/store/{storeID}")
    public ResponseEntity<List<BranchDTO>> getAllBranchesByStoreId(@PathVariable Long storeID) throws Exception {
        List<BranchDTO> branchList = branchService.getAllBranchesByStoreID(storeID);
        return ResponseEntity.ok(branchList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable Long id, @RequestBody BranchDTO branchDTO) throws Exception {
        BranchDTO getBranch = branchService.updateBranch(id, branchDTO);
        return ResponseEntity.ok(getBranch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBranchById(@PathVariable Long id) throws Exception {
        branchService.deleteBranch(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Branch deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
