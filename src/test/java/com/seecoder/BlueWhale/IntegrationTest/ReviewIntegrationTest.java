package com.seecoder.BlueWhale.IntegrationTest;

import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Review;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.ReviewRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.ReviewService;
import com.seecoder.BlueWhale.vo.ReviewVO;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ReviewIntegrationTest {

				@Autowired
				private ProductRepository productRepository;

				@Autowired
				private ReviewRepository reviewRepository;

				@Autowired
				private StoreRepository storeRepository;

				@Autowired
				private ReviewService reviewService;

				private Store store;
				private Product product;
				private ReviewVO reviewVO;

				@Before
				public void setUp() {
								// 清理现存数据
								reviewRepository.deleteAll();
								productRepository.deleteAll();
								storeRepository.deleteAll();

								// 初始化商店数据
								store = new Store();
								store.setName("Test Store");
								storeRepository.save(store);

								// 初始化商品数据
								product = new Product();
								product.setStoreId(store.getStoreId());
								product.setName("Test Product");
								productRepository.save(product);

								// 初始化评论数据
								reviewVO = new ReviewVO();
								reviewVO.setProductId(product.getProductId());
								reviewVO.setRating(5.0);
								reviewVO.setComment("Great product!");
				}

				@After
				public void tearDown() {
								// 清理测试数据
								reviewRepository.deleteAll();
								productRepository.deleteAll();
								storeRepository.deleteAll();
				}

				@Test
				public void testReview() {
								// 获取评论列表（初始应为空）
								List<ReviewVO> reviews = reviewService.getReviews(product.getProductId());
								assertTrue(reviews.isEmpty());

								// 创建评论
								Boolean createReviewResult = reviewService.createReview(reviewVO);
								assertTrue(createReviewResult);

								// 获取评论列表（应包含一个评论）
								reviews = reviewService.getReviews(product.getProductId());
								assertEquals(1, reviews.size());
								assertEquals(reviewVO.getComment(), reviews.get(0).getComment());

								// 更新商品评分
								Product updatedProduct = productRepository.findByProductId(product.getProductId());
								assertEquals(5.0, updatedProduct.getProductRating(), 0.01);
								assertEquals(1, updatedProduct.getProductRatingCount().intValue());

								// 更新商店评分
								Store updatedStore = storeRepository.findByStoreId(store.getStoreId());
								assertEquals(5.0, updatedStore.getStoreRating(), 0.01);
								assertEquals(1, updatedStore.getStoreRatingCount().intValue());

								// 创建更多评论以测试平均评分
								ReviewVO reviewVO2 = new ReviewVO();
								reviewVO2.setProductId(product.getProductId());
								reviewVO2.setRating(3.0);
								reviewVO2.setComment("Average product.");
								reviewService.createReview(reviewVO2);

								// 获取更新后的评论列表
								reviews = reviewService.getReviews(product.getProductId());
								assertEquals(2, reviews.size());

								// 更新后的商品评分
								updatedProduct = productRepository.findByProductId(product.getProductId());
								assertEquals(4.0, updatedProduct.getProductRating(), 0.01);
								assertEquals(2, updatedProduct.getProductRatingCount().intValue());

								// 更新后的商店评分
								updatedStore = storeRepository.findByStoreId(store.getStoreId());
								assertEquals(4.0, updatedStore.getStoreRating(), 0.01);
								assertEquals(2, updatedStore.getStoreRatingCount().intValue());

								ReviewVO reviewVO3 = new ReviewVO();
								reviewVO3.setProductId(product.getProductId());
								reviewVO3.setRating(1.0);
								reviewVO3.setComment("Average product.");
								reviewService.createReview(reviewVO3);

								// 获取更新后的评论列表
								reviews = reviewService.getReviews(product.getProductId());
								assertEquals(3, reviews.size());

								// 更新后的商品评分
								updatedProduct = productRepository.findByProductId(product.getProductId());
								assertEquals(3.0, updatedProduct.getProductRating(), 0.01);
								assertEquals(3, updatedProduct.getProductRatingCount().intValue());

								// 更新后的商店评分
								updatedStore = storeRepository.findByStoreId(store.getStoreId());
								assertEquals(3.0, updatedStore.getStoreRating(), 0.01);
								assertEquals(3, updatedStore.getStoreRatingCount().intValue());

				}
}
