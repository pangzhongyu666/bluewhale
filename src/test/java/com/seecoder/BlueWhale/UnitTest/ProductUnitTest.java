package com.seecoder.BlueWhale.UnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.serviceImpl.ProductServiceImpl;
import com.seecoder.BlueWhale.vo.ProductVO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class ProductUnitTest {

				@Mock
				private ProductRepository productRepository;

				@InjectMocks
				private ProductServiceImpl productService;

				@Before
				public void setUp() {
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testCreateProduct() {
								ProductVO productVO = new ProductVO();
								productVO.setName("Test Product");

								when(productRepository.save(any())).thenReturn(new Product());

								assertTrue(productService.create(productVO));

								verify(productRepository, times(1)).save(any(Product.class));
				}

				@Test
				public void testGetInfo() {
								int productId = 1;
								Product product = new Product();
								product.setProductId(productId);
								product.setName("Test Product");

								when(productRepository.findByProductId(productId)).thenReturn(product);

								ProductVO productVO = productService.getInfo(productId);

								assertNotNull(productVO);
								assertEquals(productId, (int) productVO.getProductId());
								assertEquals("Test Product", productVO.getName());
								verify(productRepository, times(1)).findByProductId(productId);
				}

				@Test
				public void testUpdateInformation() {
								int productId = 1;
								ProductVO productVO = new ProductVO();
								productVO.setProductId(productId);
								productVO.setName("Updated Product");

								Product product = new Product();
								product.setProductId(productId);

								when(productRepository.findByProductId(productId)).thenReturn(product);
								when(productRepository.save(any())).thenReturn(new Product());

								assertTrue(productService.updateInformation(productVO));
								verify(productRepository, times(1)).findByProductId(productId);
								verify(productRepository, times(1)).save(any(Product.class));
				}

				@Test
				public void testSearchProducts() {
								String name = "Test";
								ProductTypeEnum type = ProductTypeEnum.ELECTRONICS;
								Integer minPrice = 0;
								Integer maxPrice = 100;
								int page = 0;
								int size = 10;

								List<Product> products = new ArrayList<>();
								products.add(new Product());
								products.add(new Product());

								when(productRepository.findByConditions(name, type, minPrice, maxPrice, PageRequest.of(page, size))).thenReturn(new PageImpl<>(products));

								List<ProductVO> productVOs = productService.searchProducts(name, type, minPrice, maxPrice, page, size);

								assertEquals(products.size(), productVOs.size());
				}

				@Test
				public void testGetPageNum() {
								String name = "Test";
								ProductTypeEnum type = ProductTypeEnum.ELECTRONICS;
								Integer minPrice = 0;
								Integer maxPrice = 100;
								int page = 0;
								int size = 10;

								Page<Product> pageResult = new PageImpl<>(new ArrayList<>());

								when(productRepository.findByConditions(name, type, minPrice, maxPrice, PageRequest.of(page, size))).thenReturn(pageResult);

								assertEquals(1, productService.getPageNum(name, type, minPrice, maxPrice, page, size));
				}

				@Test(expected = BlueWhaleException.class)
				public void testGetInfo_NotFound() {
								int productId = 1;

								when(productRepository.findByProductId(productId)).thenReturn(null);

								ProductVO productVO = productService.getInfo(productId);

								verify(productRepository, times(1)).findByProductId(productId);
				}

				@Test(expected = BlueWhaleException.class)
				public void testUpdateInformation_NotFound() {
								int productId = 1;
								ProductVO productVO = new ProductVO();
								productVO.setProductId(productId);

								when(productRepository.findByProductId(productId)).thenReturn(null);

								boolean result = productService.updateInformation(productVO);

								verify(productRepository, times(1)).findByProductId(productId);
				}
}
