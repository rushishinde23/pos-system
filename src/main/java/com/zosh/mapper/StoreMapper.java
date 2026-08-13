package com.zosh.mapper;

import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;

public class StoreMapper {
    public static Storedto toDTO(Store store){
        Storedto storedto = new Storedto();
        storedto.setId(store.getId());
        storedto.setBrand(store.getBrand());
        storedto.setDescription(store.getDescription());
        storedto.setStoreAdmin(UserMapper.toDTO(store.getStoreAdmin()));
        storedto.setStoreType(store.getStoreType());
        storedto.setContact(store.getContact());
        storedto.setCreatedAt(store.getCreatedAt());
        storedto.setUpdatedAt(store.getUpdatedAt());
        storedto.setStatus(store.getStatus());
        return storedto;
    }

    public static Store toEntity(Storedto storedto , User storeAdmin)
    {
         Store store = new Store();
        store.setId(storedto.getId());
        store.setBrand(storedto.getBrand());
        store.setDescription(storedto.getDescription());
        store.setStoreAdmin(storeAdmin);
        store.setStoreType(storedto.getStoreType());
        store.setContact(storedto.getContact());
        store.setCreatedAt(storedto.getCreatedAt());
        store.setUpdatedAt(storedto.getUpdatedAt());

        return store;
    }
}
