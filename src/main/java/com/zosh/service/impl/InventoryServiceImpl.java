/*
package com.zosh.service.impl;

import com.zosh.modal.Branch;
import com.zosh.modal.Product;
import com.zosh.payload.dto.InventoryDTO;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.InventoryRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) throws Exception {
        Branch branch = branchRepository.findById(inventoryDTO.getBranchId()).orElseThrow(
                () -> new Exception("Branch not exist")
        );
        Product product = productRepository.findById(inventoryDTO.getProductId())
                .orElseThrow(()-> new Exception("product not exist"));


        return null;
    }

    @Override
    public InventoryDTO updateInventory(InventoryDTO inventoryDTO) {
        return null;
    }

    @Override
    public void deleteInventory(Long id) {

    }

    @Override
    public InventoryDTO getInventory(Long id) {
        return null;
    }

    @Override
    public InventoryDTO getInventoryByProductIdAndBranchId(Long productId, Long branchId) {
        return null;
    }

    @Override
    public List<InventoryDTO> getAllInventoryByBranchId(Long branchId) {
        return List.of();
    }
}
*/

package com.zosh.service.impl;

import com.zosh.mapper.InventoryMapper;
import com.zosh.modal.Branch;
import com.zosh.modal.Inventory;
import com.zosh.modal.Product;
import com.zosh.payload.dto.InventoryDTO;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.InventoryRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
//This is service implementation
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) throws Exception {

        Branch branch = branchRepository.findById(inventoryDTO.getBranchId())
                .orElseThrow(() -> new Exception("Branch not found"));

        Product product = productRepository.findById(inventoryDTO.getProductId())
                .orElseThrow(() -> new Exception("Product not found"));

        Inventory inventory = InventoryMapper.toEntity(inventoryDTO, branch, product);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toDTO(savedInventory);
    }

    @Override
    public InventoryDTO updateInventory(Long id ,InventoryDTO inventoryDTO) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found..."));

        inventory.setQuantity(inventoryDTO.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public void deleteInventory(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDTO getInventory(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found..."));

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public InventoryDTO getInventoryByProductIdAndBranchId(Long productId, Long branchId) {

        Inventory inventory = inventoryRepository.
                findByProductIdAndBranchId(productId, branchId);


        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventoryByBranchId(Long branchId) {

        List<Inventory> inventories = inventoryRepository.findByBranchId(branchId);
        return inventories.stream().map(
                InventoryMapper::toDTO
        ).collect(Collectors.toList());
    }
}
