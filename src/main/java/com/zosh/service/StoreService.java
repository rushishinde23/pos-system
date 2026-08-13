package com.zosh.service;

import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;

import java.util.List;

public interface StoreService {

    Storedto createStore(Storedto storeDTO , User user);
    Storedto gerStoreById(Long id) throws Exception;
    List<Storedto> getAllStores();
    Store getStoreByAdmin() throws UserException;
    Storedto updateStore(Long id ,Storedto storeDTO ) throws UserException;
    void deleteStore(Long id) throws UserException;
    Storedto getStoreByEmployee() throws UserException;

    Storedto moderateStore(Long id, StoreStatus status) throws Exception;



}
