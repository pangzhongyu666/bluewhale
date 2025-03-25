package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {

				Store findByName(String name);
				Store findByStoreId(Integer storeId);
}
