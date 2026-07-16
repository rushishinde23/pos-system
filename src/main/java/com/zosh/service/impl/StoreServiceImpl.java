package com.zosh.service.impl;

import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.mapper.StoreMapper;
import com.zosh.modal.Store;
import com.zosh.modal.StoreContact;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;
import com.zosh.repository.StoreRepository;
import com.zosh.service.StoreService;
import com.zosh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public Storedto createStore(Storedto storeDTO, User user) {

        Store store = StoreMapper.toEntity(storeDTO, user);

        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public Storedto gerStoreById(Long id) throws Exception {

        Store store = storeRepository.findById(id).orElseThrow(
                ()-> new Exception("Store not found")
        );
        return StoreMapper.toDTO(store);
    }

    @Override
    public List<Storedto> getAllStores() {
        List<Store> dtos = storeRepository.findAll();
        return dtos.stream().map(StoreMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Store getStoreByAdmin() throws UserException {
        User admin = userService.getCurrentUser();
        return storeRepository.findByStoreAdminId(admin.getId());
    }

    @Override
    public Storedto updateStore(Long id, Storedto storeDTO) throws UserException {
        User curresntUser = userService.getCurrentUser();
        Store existing = storeRepository.findByStoreAdminId(curresntUser.getId());
        if (existing==null){
            throw new UserException("Store not found");
        }
        existing.setBrand(storeDTO.getBrand());
        existing.setDescription(storeDTO.getDescription());
        if (storeDTO.getStoreType()!= null){
            existing.setStoreType(storeDTO.getStoreType());
        }
        if(storeDTO.getContact()!=null){
            StoreContact contact = StoreContact.builder().address(storeDTO.getContact().getAddress())
                    .phone(storeDTO.getContact().getPhone())
                    .email(storeDTO.getContact().getEmail())
                    .build();
            existing.setContact(contact);
        }
        Store updatedStore = storeRepository.save(existing);

        return StoreMapper.toDTO(updatedStore);
    }

    @Override
    public void deleteStore(Long id) throws UserException {
        Store store = getStoreByAdmin();
      storeRepository.delete(store);
    }

    @Override
    public Storedto getStoreByEmployee() throws UserException {
         User currentUser = userService.getCurrentUser();
         if (currentUser==null){
             throw new UserException("you don't have permission to access this store");
         }
                 return StoreMapper.toDTO(currentUser.getStore());
    }

    @Override
    public Storedto moderateStore(Long id, StoreStatus status) throws Exception {

        Store store = storeRepository.findById(id).orElseThrow(
                ()-> new Exception("store not found...")
        );

        store.setStatus(status);
        Store updatedStore = storeRepository.save(store);
        return StoreMapper.toDTO(updatedStore);
    }
}
