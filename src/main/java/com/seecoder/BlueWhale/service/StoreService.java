package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;

import java.util.List;

public interface StoreService {

				Boolean create (StoreVO storeVO);

				List<StoreVO> getAllStores();
				List<ProductVO> getOneStoreProducts(Integer storeId);

				StoreVO getInfo(Integer storeId);
}
