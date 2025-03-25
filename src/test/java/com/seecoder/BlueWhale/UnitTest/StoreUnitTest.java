package com.seecoder.BlueWhale.UnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.serviceImpl.StoreServiceImpl;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StoreUnitTest {

				@Mock
				private StoreRepository storeRepository;

				@Mock
				private ProductRepository productRepository;

				@InjectMocks
				private StoreServiceImpl storeService;

				@Before
				public void setUp() {
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testCreateStore() {
								StoreVO storeVO = new StoreVO();
								storeVO.setName("Test Store");

								when(storeRepository.findByName(storeVO.getName())).thenReturn(null);

								assertTrue(storeService.create(storeVO));
								verify(storeRepository, times(1)).findByName(storeVO.getName());
								verify(storeRepository, times(1)).save(any(Store.class));
				}

				@Test(expected = BlueWhaleException.class)
				public void testCreateExistingStore() {
								StoreVO storeVO = new StoreVO();
								storeVO.setName("Existing Store");

								when(storeRepository.findByName(storeVO.getName())).thenReturn(new Store());

								storeService.create(storeVO);
				}

				@Test
				public void testGetAllStores() {
								List<Store> stores = new ArrayList<>();
								stores.add(new Store());
								stores.add(new Store());

								when(storeRepository.findAll()).thenReturn(stores);

								List<StoreVO> storeVOs = storeService.getAllStores();

								assertEquals(stores.size(), storeVOs.size());
								verify(storeRepository, times(1)).findAll();
				}

				@Test
				public void testGetOneStoreProducts() {
								int storeId = 1;
								Store store = new Store();
								store.setStoreId(storeId);

								List<Product> products = new ArrayList<>();
								Product product1 = new Product();
								product1.setName("Product 1");
								products.add(product1);
								Product product2 = new Product();
								product2.setName("Product 2");
								products.add(product2);

								when(storeRepository.findByStoreId(storeId)).thenReturn(store);
								when(productRepository.findByStoreId(storeId)).thenReturn(products);

								List<ProductVO> productVOs = storeService.getOneStoreProducts(storeId);

								assertNotNull(productVOs);
								assertEquals(products.size(), productVOs.size());
								assertEquals("Product 1", productVOs.get(0).getName());
								assertEquals("Product 2", productVOs.get(1).getName());
								verify(storeRepository, times(1)).findByStoreId(storeId);
								verify(productRepository, times(1)).findByStoreId(storeId);
				}

				@Test(expected = BlueWhaleException.class)
				public void testGetOneStoreProducts_StoreNotFound() {
								int storeId = 1;

								when(storeRepository.findByStoreId(storeId)).thenReturn(null);

								storeService.getOneStoreProducts(storeId);
				}
				@Test
				public void testGetInfo() {
								int storeId = 1;
								Store store = new Store();
								store.setStoreId(storeId);
								store.setName("Test Store");

								when(storeRepository.findByStoreId(storeId)).thenReturn(store);

								StoreVO storeVO = storeService.getInfo(storeId);

								assertNotNull(storeVO);
								assertEquals(storeId, (int) storeVO.getStoreId());
								assertEquals("Test Store", storeVO.getName());
								verify(storeRepository, times(1)).findByStoreId(storeId);
				}

				@Test(expected = BlueWhaleException.class)
				public void testGetInfo_StoreNotFound() {
								int storeId = 1;

								when(storeRepository.findByStoreId(storeId)).thenReturn(null);

								storeService.getInfo(storeId);
				}
}
