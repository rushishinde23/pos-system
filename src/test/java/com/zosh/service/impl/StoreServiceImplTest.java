/*
package com.zosh.service.impl;

import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.modal.Store;
import com.zosh.modal.StoreContact;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;
import com.zosh.repository.StoreRepository;
import com.zosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private StoreServiceImpl storeService;

    private User admin;
    private Store store;
    private Storedto storeDTO;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);

        store = new Store();
        store.setId(1L);
        store.setBrand("Old Brand");

        storeDTO = new Storedto();
        storeDTO.setBrand("New Brand");
        storeDTO.setDescription("New Description");
    }

    @Test
    void createStore_savesAndReturns() {
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        Storedto result = storeService.createStore(storeDTO, admin);
        assertNotNull(result);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void gerStoreById_success() throws Exception {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        Storedto result = storeService.gerStoreById(1L);
        assertNotNull(result);
    }

    @Test
    void gerStoreById_notFound_throws() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> storeService.gerStoreById(99L));
    }

    @Test
    void getAllStores_returnsList() {
        when(storeRepository.findAll()).thenReturn(Arrays.asList(store, new Store()));
        List<Storedto> result = storeService.getAllStores();
        assertEquals(2, result.size());
    }

    @Test
    void getStoreByAdmin_returnsStore() throws UserException {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(storeRepository.findByStoreAdminId(1L)).thenReturn(store);
        assertEquals(store, storeService.getStoreByAdmin());
    }

    @Test
    void updateStore_success_updatesFieldsIncludingContact() throws UserException {
        storeDTO.setContact(new StoreContact());
        storeDTO.getContact().setAddress("123 St");
        storeDTO.getContact().setPhone("999");
        storeDTO.getContact().setEmail("s@example.com");

        when(userService.getCurrentUser()).thenReturn(admin);
        when(storeRepository.findByStoreAdminId(1L)).thenReturn(store);
        when(storeRepository.save(store)).thenReturn(store);

        Storedto result = storeService.updateStore(1L, storeDTO);

        assertNotNull(result);
        assertEquals("New Brand", store.getBrand());
        assertNotNull(store.getContact());
    }

    @Test
    void updateStore_notFound_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(storeRepository.findByStoreAdminId(1L)).thenReturn(null);
        assertThrows(UserException.class, () -> storeService.updateStore(1L, storeDTO));
    }

    @Test
    void deleteStore_success() throws UserException {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(storeRepository.findByStoreAdminId(1L)).thenReturn(store);

        storeService.deleteStore(1L);

        verify(storeRepository).delete(store);
    }

    @Test
    void getStoreByEmployee_success() throws UserException {
        User employee = new User();
        employee.setStore(store);
        when(userService.getCurrentUser()).thenReturn(employee);

        Storedto result = storeService.getStoreByEmployee();

        assertNotNull(result);
    }

    @Test
    void moderateStore_success() throws Exception {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(storeRepository.save(store)).thenReturn(store);

        Storedto result = storeService.moderateStore(1L, StoreStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(StoreStatus.ACTIVE, store.getStatus());
    }

    @Test
    void moderateStore_notFound_throws() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> storeService.moderateStore(99L, StoreStatus.ACTIVE));
    }
}*/
package com.zosh.service.impl;


import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.modal.*;
import com.zosh.payload.dto.Storedto;
import com.zosh.repository.StoreRepository;
import com.zosh.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class StoreServiceImplTest {


    @Mock
    private StoreRepository storeRepository;


    @Mock
    private UserService userService;


    @InjectMocks
    private StoreServiceImpl storeService;


    private User user;
    private Store store;
    private Storedto storeDTO;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);


        user = new User();
        user.setId(1L);


        storeDTO = new Storedto();
        storeDTO.setId(10L);
        storeDTO.setBrand("Zosh Store");
        storeDTO.setDescription("POS Store");


        store = new Store();
        store.setId(10L);
        store.setBrand("Zosh Store");
        store.setDescription("POS Store");
        store.setStoreAdmin(user);

    }



    // ================= CREATE STORE =================


    @Test
    void shouldCreateStoreSuccessfully(){


        when(storeRepository.save(any(Store.class)))
                .thenReturn(store);



        var result =
                storeService.createStore(storeDTO,user);



        assertNotNull(result);

        assertEquals(
                "Zosh Store",
                result.getBrand()
        );


        verify(storeRepository)
                .save(any(Store.class));

    }





    // ================= GET BY ID =================


    @Test
    void shouldGetStoreByIdSuccessfully() throws Exception{


        when(storeRepository.findById(10L))
                .thenReturn(Optional.of(store));



        Storedto result =
                storeService.gerStoreById(10L);



        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

    }





    @Test
    void shouldThrowExceptionWhenStoreNotFound(){


        when(storeRepository.findById(99L))
                .thenReturn(Optional.empty());



        Exception exception =
                assertThrows(
                        Exception.class,
                        () ->
                                storeService.gerStoreById(99L)
                );


        assertEquals(
                "Store not found",
                exception.getMessage()
        );

    }





    // ================= GET ALL =================


    @Test
    void shouldGetAllStores(){


        when(storeRepository.findAll())
                .thenReturn(
                        List.of(store)
                );



        List<Storedto> result =
                storeService.getAllStores();



        assertEquals(
                1,
                result.size()
        );

    }





    // ================= GET STORE BY ADMIN =================


    @Test
    void shouldGetStoreByAdmin() throws UserException {


        when(userService.getCurrentUser())
                .thenReturn(user);



        when(storeRepository.findByStoreAdminId(1L))
                .thenReturn(store);



        Store result =
                storeService.getStoreByAdmin();



        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

    }





    // ================= UPDATE STORE =================


    @Test
    void shouldUpdateStoreSuccessfully()
            throws UserException {



        when(userService.getCurrentUser())
                .thenReturn(user);



        when(storeRepository.findByStoreAdminId(1L))
                .thenReturn(store);



        when(storeRepository.save(any(Store.class)))
                .thenReturn(store);



        Storedto updateDTO =
                new Storedto();

        updateDTO.setBrand("Updated Store");



        Storedto result =
                storeService.updateStore(
                        10L,
                        updateDTO
                );



        assertNotNull(result);


        verify(storeRepository)
                .save(any(Store.class));

    }





    @Test
    void shouldThrowExceptionWhenUpdatingMissingStore()
            throws UserException {



        when(userService.getCurrentUser())
                .thenReturn(user);



        when(storeRepository.findByStoreAdminId(1L))
                .thenReturn(null);



        Exception exception =
                assertThrows(
                        UserException.class,
                        () ->
                                storeService.updateStore(
                                        10L,
                                        storeDTO
                                )
                );



        assertEquals(
                "Store not found",
                exception.getMessage()
        );

    }





    // ================= DELETE STORE =================


    @Test
    void shouldDeleteStoreSuccessfully()
            throws UserException {


        when(userService.getCurrentUser())
                .thenReturn(user);



        when(storeRepository.findByStoreAdminId(1L))
                .thenReturn(store);



        storeService.deleteStore(10L);



        verify(storeRepository)
                .delete(store);

    }





    // ================= GET EMPLOYEE STORE =================


    @Test
    void shouldGetStoreByEmployee()
            throws UserException {


        user.setStore(store);



        when(userService.getCurrentUser())
                .thenReturn(user);



        Storedto result =
                storeService.getStoreByEmployee();



        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

    }





    @Test
    void shouldThrowExceptionWhenEmployeeIsNull()
            throws UserException {


        when(userService.getCurrentUser())
                .thenReturn(null);



        Exception exception =
                assertThrows(
                        UserException.class,
                        () ->
                                storeService.getStoreByEmployee()
                );



        assertEquals(
                "you don't have permission to access this store",
                exception.getMessage()
        );

    }





    // ================= MODERATE STORE =================


    @Test
    void shouldModerateStoreSuccessfully()
            throws Exception {



        when(storeRepository.findById(10L))
                .thenReturn(Optional.of(store));



        when(storeRepository.save(any(Store.class)))
                .thenReturn(store);



        Storedto result =
                storeService.moderateStore(
                        10L,
                        StoreStatus.ACTIVE
                );



        assertNotNull(result);



        verify(storeRepository)
                .save(store);

    }





    @Test
    void shouldThrowExceptionWhenModerateStoreNotFound(){


        when(storeRepository.findById(99L))
                .thenReturn(Optional.empty());



        Exception exception =
                assertThrows(
                        Exception.class,
                        () ->
                                storeService.moderateStore(
                                        99L,
                                        StoreStatus.ACTIVE
                                )
                );



        assertEquals(
                "store not found...",
                exception.getMessage()
        );

    }


}
