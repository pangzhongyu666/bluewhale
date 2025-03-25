package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.repository.ProductRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProductSearchTests {
				@Autowired
				private ProductRepository productRepository;

				@Before
				public void setUp() {
								// 清理现存数据
								productRepository.deleteAll();
				}

				@After
				public void tearDown() {
								// 清理测试数据
								productRepository.deleteAll();
				}

				@Test
				public void testFindByConditions1() {
								Pageable pageable = PageRequest.of(0, 1000);
								// 创建一些测试数据
								Product product1 = new Product();
								product1.setName("T-shirt");
								product1.setType(ProductTypeEnum.CLOTHES);
								product1.setPrice(50);
								productRepository.save(product1);

								Product product2 = new Product();
								product2.setName("Jeans");
								product2.setType(ProductTypeEnum.CLOTHES);
								product2.setPrice(80);
								productRepository.save(product2);

								Product product3 = new Product();
								product3.setName("Laptop");
								product3.setType(ProductTypeEnum.ELECTRONICS);
								product3.setPrice(1000);
								productRepository.save(product3);

								// 测试不传递任何条件
								List<Product> products = productRepository.findByConditions(null, null, null, null,pageable).getContent();
								assertEquals(3, products.size());

								// 测试按名称查询
								products = productRepository.findByConditions("shirt", null, null, null,pageable).getContent();
								assertEquals(1, products.size());
								assertEquals("T-shirt", products.get(0).getName());

								// 测试按类型查询
								products = productRepository.findByConditions(null, ProductTypeEnum.CLOTHES, null, null,pageable).getContent();
								assertEquals(2, products.size());

								// 测试按价格区间查询
								products = productRepository.findByConditions(null, null, 50, 100,pageable).getContent();
								assertEquals(2, products.size());

								// 测试组合条件查询
								products = productRepository.findByConditions("jeans", ProductTypeEnum.CLOTHES, 70, 90,pageable).getContent();
								assertEquals(1, products.size());
								assertEquals("Jeans", products.get(0).getName());
				}

				@Test
				public void testFindByConditions2() {
								Pageable pageable = PageRequest.of(0, 1000);
								// 创建一些测试数据
								Product product1 = new Product();
								product1.setName("华为mate30");
								product1.setType(ProductTypeEnum.ELECTRONICS);
								product1.setPrice(5000);
								productRepository.save(product1);


								Product product2 = new Product();
								product2.setName("苹果12");
								product2.setType(ProductTypeEnum.ELECTRONICS);
								product2.setPrice(8000);
								productRepository.save(product2);

								Product product3 = new Product();
								product3.setName("拯救者");
								product3.setType(ProductTypeEnum.ELECTRONICS);
								product3.setPrice(10000);
								productRepository.save(product3);

								Product product4 = new Product();
								product4.setName("4090ti");
								product4.setType(ProductTypeEnum.ELECTRONICS);
								product4.setPrice(12000);
								productRepository.save(product4);

								Product product5 = new Product();
								product5.setName("华为p60");
								product5.setType(ProductTypeEnum.ELECTRONICS);
								product5.setPrice(7000);
								productRepository.save(product5);

								// 测试不传递任何条件
								List<Product> products = productRepository.findByConditions(null, null, null, null,pageable).getContent();
								assertEquals(5, products.size());

								// 测试按名称模糊查询
								products = productRepository.findByConditions("华为", null, null, null,pageable).getContent();
								assertEquals(2, products.size());
								assertEquals("华为mate30", products.get(0).getName());
								assertEquals("华为p60", products.get(1).getName());

								products = productRepository.findByConditions("0", null, null, null,pageable).getContent();
								assertEquals(3, products.size());
								// 测试按类型查询
								products = productRepository.findByConditions(null, ProductTypeEnum.ELECTRONICS, null, null,pageable).getContent();
								assertEquals(5, products.size());

								// 测试按价格区间查询
								products = productRepository.findByConditions(null, null, 8000, 10000,pageable).getContent();
								assertEquals(2, products.size());

								// 测试组合条件查询
								products = productRepository.findByConditions("华为", ProductTypeEnum.ELECTRONICS, 4000, 6000,pageable).getContent();
								assertEquals(1, products.size());
								assertEquals("华为mate30", products.get(0).getName());
				}


}
