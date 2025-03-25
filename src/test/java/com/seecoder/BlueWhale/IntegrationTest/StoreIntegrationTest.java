package com.seecoder.BlueWhale.IntegrationTest;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class StoreIntegrationTest {

				@Autowired
				private StoreRepository storeRepository;

				@Autowired
				private ProductRepository productRepository;

				@Autowired
				private StoreServiceImpl storeService;

				private StoreVO storeVO1;
				private StoreVO storeVO2;
				private ProductVO productVO1;
				private ProductVO productVO2;

				@Before
				public void setUp() {
								// 清理现存数据
								productRepository.deleteAll();
								storeRepository.deleteAll();

								// 初始化商店数据
								storeVO1 = new StoreVO();
								storeVO1.setName("Test Store1");

								storeVO2 = new StoreVO();
								storeVO2.setName("Test Store2");

								// 初始化商品数据
								productVO1 = new ProductVO();
								productVO1.setName("Test Product1");
								productVO1.setPrice(100);
								productVO1.setType(ProductTypeEnum.ELECTRONICS);
								productVO1.setDescription("Description for Test Product1");
								productVO1.setInventory(10);

								productVO2 = new ProductVO();
								productVO2.setName("Test Product2");
								productVO2.setPrice(200);
								productVO2.setType(ProductTypeEnum.CLOTHES);
								productVO2.setDescription("Description for Test Product2");
								productVO2.setInventory(20);
				}

				@Test
				public void testStore() {
								Store existingStore = storeRepository.findByName(storeVO1.getName());
								assertNull(existingStore);

								// 创建商店1
								Boolean result1 = storeService.create(storeVO1);
								assertTrue(result1);

								// 创建商店2
								Boolean result2 = storeService.create(storeVO2);
								assertTrue(result2);

								Store newStore1 = storeRepository.findByName(storeVO1.getName());
								assertNotNull(newStore1);
								assertEquals(storeVO1.getName(), newStore1.getName());

								Store newStore2 = storeRepository.findByName(storeVO2.getName());
								assertNotNull(newStore2);
								assertEquals(storeVO2.getName(), newStore2.getName());

								// 已存在商店名报错
								assertThrows(BlueWhaleException.class, () -> storeService.create(storeVO1));

								// 获取所有商店信息
								List<StoreVO> allStores = storeService.getAllStores();
								assertNotNull(allStores);
								assertEquals(2, allStores.size());

								// 获取单个商店信息
								StoreVO retrievedStoreVO = storeService.getInfo(newStore1.getStoreId());
								assertEquals(storeVO1.getName(), retrievedStoreVO.getName());

								// 添加商品到商店1
								productVO1.setStoreId(newStore1.getStoreId());
								productRepository.save(productVO1.toPO());

								productVO2.setStoreId(newStore1.getStoreId());
								productRepository.save(productVO2.toPO());

								// 获取商店1的所有商品
								List<ProductVO> store1Products = storeService.getOneStoreProducts(newStore1.getStoreId());
								assertNotNull(store1Products);
								assertEquals(2, store1Products.size());
				}
}
