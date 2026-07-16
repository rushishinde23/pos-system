package com.zosh.controller;

import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.mapper.StoreMapper;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.StoreService;
import com.zosh.service.UserService;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Storedto> createStore(@RequestBody Storedto storedto,
                                                @RequestHeader("Authorization") String jwt ) throws UserException {
        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(storeService.createStore(storedto,user));
    }



    @GetMapping
    public ResponseEntity<List<Storedto>> getAllStore(
            @RequestHeader("Authorization") String jwt ) throws UserException {

        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/admin")
    public ResponseEntity<Storedto> getStoreByAdmin(
            @RequestHeader("Authorization") String jwt ) throws UserException
    {

        return ResponseEntity.ok(StoreMapper.toDTO(storeService.getStoreByAdmin()));
    }

    @GetMapping("/employee")
    public ResponseEntity<Storedto> getStoreByEmployee(
            @RequestHeader("Authorization") String jwt ) throws UserException
    {

        return ResponseEntity.ok(storeService.getStoreByEmployee());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Storedto> updateStore(@PathVariable Long id ,
                                                @RequestBody Storedto storedto)throws Exception{
        return ResponseEntity.ok(storeService.updateStore(id , storedto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id )throws Exception
    {
        storeService.deleteStore(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Store deleted Successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/moderate")
    public ResponseEntity<Storedto> moderateStore(@PathVariable Long id ,
                                                @RequestParam StoreStatus status,
                                                @RequestBody Storedto storedto)
            throws Exception{
        return ResponseEntity.ok(storeService.moderateStore(id, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Storedto> getStoreBYId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt ) throws Exception
    {
        return ResponseEntity.ok(storeService.gerStoreById(id));
    }


}
