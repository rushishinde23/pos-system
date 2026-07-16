package com.zosh.repository;

import com.zosh.modal.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {
 Store findByStoreAdminId(Long adminId);


}
