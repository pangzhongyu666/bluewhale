package com.seecoder.BlueWhale.UnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Review;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.ReviewRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.serviceImpl.ReviewServiceImpl;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.ReviewVO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReviewUnitTest {

				@Mock
				private ProductRepository productRepository;

				@Mock
				private ReviewRepository reviewRepository;

				@Mock
				private StoreRepository storeRepository;

				@Mock
				private StoreService storeService;

				@InjectMocks
				private ReviewServiceImpl reviewService;

				@Before
				public void setUp() {
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testGetReviews() {
								int productId = 1;
								List<Review> reviews = new ArrayList<>();
								reviews.add(new Review());
								reviews.add(new Review());

								when(reviewRepository.findByProductId(productId)).thenReturn(reviews);

								List<ReviewVO> reviewVOs = reviewService.getReviews(productId);

								assertEquals(reviews.size(), reviewVOs.size());
				}

				@Test
				public void testCreateReview() {
								ReviewVO reviewVO = new ReviewVO();
								reviewVO.setProductId(1);

								Product product = new Product();
								product.setProductId(reviewVO.getProductId());

								Store store = new Store();
								store.setStoreId(1);

								when(productRepository.findByProductId(reviewVO.getProductId())).thenReturn(product);
								when(storeRepository.findByStoreId(product.getStoreId())).thenReturn(store);
								when(reviewRepository.save(any(Review.class))).thenReturn(new Review());
								when(storeService.getOneStoreProducts(store.getStoreId())).thenReturn(new ArrayList<>());

								assertTrue(reviewService.createReview(reviewVO));
				}

				@Test
				public void testUpdateReviewForStore() {
								Store store = new Store();
								store.setStoreId(1);

								List<ProductVO> productList = new ArrayList<>();
								ProductVO product1 = new ProductVO();
								product1.setProductRating(4.0);
								product1.setProductRatingCount(10);
								productList.add(product1);

								ProductVO product2 = new ProductVO();
								product2.setProductRating(5.0);
								product2.setProductRatingCount(5);
								productList.add(product2);

								when(storeService.getOneStoreProducts(store.getStoreId())).thenReturn(productList);

								reviewService.updateReview(store);

								assertEquals(4.5, store.getStoreRating(), 0.01);
								assertEquals(15, (int)store.getStoreRatingCount());
								verify(storeRepository, times(1)).save(store);
				}
				@Test
				public void testUpdateReviewForProduct() {
								Product product = new Product();
								product.setProductId(1);

								List<Review> reviewList = new ArrayList<>();
								Review review1 = new Review();
								review1.setRating(4.0);
								reviewList.add(review1);

								Review review2 = new Review();
								review2.setRating(5.0);
								reviewList.add(review2);

								when(reviewRepository.findByProductId(product.getProductId())).thenReturn(reviewList);

								reviewService.updateReview(product);

								assertEquals(4.50, product.getProductRating(), 0.01);
								assertEquals(2, (int)product.getProductRatingCount());
								verify(productRepository, times(1)).save(product);
				}
}
