package com.seecoder.BlueWhale.IntegrationTest;

import static org.junit.Assert.*;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.serviceImpl.ProductServiceImpl;
import com.seecoder.BlueWhale.vo.ProductVO;
import org.junit.After;
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
public class ProductIntegrationTest {

				@Autowired
				private ProductRepository productRepository;

				@Autowired
				private ProductServiceImpl productService;

				private ProductVO productVO1;
				private ProductVO productVO2;

				@Before
				public void setUp() {
								// 清理现存数据
								productRepository.deleteAll();

								// 初始化商品数据
								productVO1 = new ProductVO();
								productVO1.setName("Test Product1");
								productVO1.setPrice(100);
								productVO1.setType(ProductTypeEnum.ELECTRONICS);
								productVO1.setDescription("Description for Test Product1");
								productVO1.setInventory(10);
								productVO1.setStoreId(1);

								productVO2 = new ProductVO();
								productVO2.setName("Test Product2");
								productVO2.setPrice(200);
								productVO2.setType(ProductTypeEnum.CLOTHES);
								productVO2.setDescription("Description for Test Product2");
								productVO2.setInventory(20);
								productVO2.setStoreId(2);

				}
				@After
				public void tearDown() {
								// 清理测试数据
								productRepository.deleteAll();
				}
				@Test
				public void testProduct() {
								// 创建商品1
								Boolean result1 = productService.create(productVO1);
								assertTrue(result1);

								// 创建商品2
								Boolean result2 = productService.create(productVO2);
								assertTrue(result2);

								Product newProduct1 = productRepository.findByStoreId(1).get(0);
								assertNotNull(newProduct1);
								assertEquals(productVO1.getName(), newProduct1.getName());
								assertEquals(productVO1.getPrice(), newProduct1.getPrice());
								assertEquals(productVO1.getType(), newProduct1.getType());
								assertEquals(productVO1.getDescription(), newProduct1.getDescription());
								assertEquals(productVO1.getInventory(), newProduct1.getInventory());

								Product newProduct2 = productRepository.findByStoreId(2).get(0);
								assertNotNull(newProduct2);
								assertEquals(productVO2.getName(), newProduct2.getName());
								assertEquals(productVO2.getPrice(), newProduct2.getPrice());
								assertEquals(productVO2.getType(), newProduct2.getType());
								assertEquals(productVO2.getDescription(), newProduct2.getDescription());
								assertEquals(productVO2.getInventory(), newProduct2.getInventory());


								// 获取商品信息
								ProductVO retrievedProductVO = productService.getInfo(newProduct1.getProductId());
								assertEquals(productVO1.getName(), retrievedProductVO.getName());
								assertEquals(productVO1.getPrice(), retrievedProductVO.getPrice());
								assertEquals(productVO1.getType(), retrievedProductVO.getType());
								assertEquals(productVO1.getDescription(), retrievedProductVO.getDescription());
								assertEquals(productVO1.getInventory(), retrievedProductVO.getInventory());
								assertEquals(productVO1.getStoreId(), retrievedProductVO.getStoreId());


								// 更新商品信息
								ProductVO updateInfo = new ProductVO();
								updateInfo.setProductId(newProduct1.getProductId());
								updateInfo.setPrice(150);
								updateInfo.setName("Test ProductName");
								updateInfo.setDescription("Updated description for Test Product1");
								productService.updateInformation(updateInfo);

								ProductVO updatedProduct = productService.getInfo(newProduct1.getProductId());
								assertNotNull(updatedProduct);
								assertEquals(updateInfo.getPrice(), updatedProduct.getPrice());
								assertEquals(updateInfo.getName(), updatedProduct.getName());
								assertEquals(updateInfo.getDescription(), updatedProduct.getDescription());

								// 搜索商品
								//具体的搜索商品测试有另一个文件
								List<ProductVO> searchResults = productService.searchProducts("Test", ProductTypeEnum.ELECTRONICS, 50, 200, 0, 10);
								assertNotNull(searchResults);
								assertEquals(1, searchResults.size());
								assertEquals(updatedProduct.getName(), searchResults.get(0).getName());

								// 获取分页数量
								int totalPages = productService.getPageNum("Test", ProductTypeEnum.ELECTRONICS, 50, 200, 0, 10);
								assertEquals(1, totalPages);
				}
}
