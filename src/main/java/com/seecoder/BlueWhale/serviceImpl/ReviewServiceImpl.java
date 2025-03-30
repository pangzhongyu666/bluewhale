package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Review;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.ReviewRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.ReviewService;
import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.ReviewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

				@Autowired
				ProductRepository productRepository;
				@Autowired
				ReviewRepository reviewRepository;
				@Autowired
				StoreRepository storeRepository;
				@Autowired
				StoreService storeService;
				private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

				@Autowired
				private RedisTemplate redisTemplate;
				@Override
				public List<ReviewVO> getReviews(Integer productId) {//获取商品评论
								//从redis hash缓存中获取
								String key = "reviewsOfProduct" + productId;

								//如果缓存中存在，则直接返回
								if(redisTemplate.hasKey(key)){
												logger.info("从redis中获取评论");
												List<ReviewVO> reviewVOs = new ArrayList<>();
												for(Object o : redisTemplate.opsForHash().values(key)){
																ReviewVO reviewVO = (ReviewVO) o;
																reviewVO.setReviewImages(reviewVO.getReviewImagesForRedis());
																reviewVO.setReviewImagesForRedis(null);
																reviewVOs.add(reviewVO);
												}
												return reviewVOs;
								}

								//如果缓存中不存在，则从数据库中获取
								List<Review> reviews = reviewRepository.findByProductId(productId);
								//将评论存入redis hash缓存中
								List<ReviewVO> reviewVOs = new ArrayList<>();
								for(Review review : reviews){
												ReviewVO reviewVO = review.toVO();
												reviewVO.setReviewImages(null);
												redisTemplate.opsForHash().put(key, reviewVO.getReviewId() + "", reviewVO);
												reviewVOs.add(reviewVO);
								}

								return reviews.stream().map(Review::toVO).collect(Collectors.toList());
				}

				@Override
				public List<ReviewVO> getChildrenReviews(Integer reviewId) {//获取子评论
								return reviewRepository.findByParentId(reviewId).stream().map(Review::toVO).collect(Collectors.toList());
				}

				@Override
				public Boolean createReview(ReviewVO reviewVO) {
								String key = "reviewsOfProduct" + reviewVO.getProductId();
								if(reviewVO.getParentId() == null){
												reviewVO.setParentId(0);//防止父评论id为空
								}
								Review review = reviewVO.toPO();
								reviewRepository.save(review);

								if(reviewVO.getParentId() != 0){//子评论不计入更新评分
												return true;
								}else{
												//将评论存入redis hash缓存中
												ReviewVO reviewVO1 = review.toVO();
												reviewVO1.setReviewImages(null);
												redisTemplate.opsForHash().put(key, reviewVO1.getReviewId() + "", reviewVO1);
								}
								Product product = productRepository.findByProductId(reviewVO.getProductId());
								updateReview(product);
								Store store = storeRepository.findByStoreId(product.getStoreId());
								updateReview(store);
								logger.info("创建评论" + review.getReviewId());
								return true;
				}
				@Transactional
				public void updateReview(Store store) {
								List<ProductVO> productList = storeService.getOneStoreProducts(store.getStoreId());
								double storeRating = productList//商店评分
																.stream()
																.filter(ProductVO -> ProductVO.getProductRating() != null)//商品没有评分的不纳入平均计算
																.mapToDouble(ProductVO::getProductRating)
																.average()
																.orElse(0.0);
								storeRating = Double.parseDouble(String.format("%.2f",storeRating));//格式化评分为两位数
								int storeRatingCount = productList//商店评分数量
																.stream()
																.filter(ProductVO -> ProductVO.getProductRatingCount() != null)
																.mapToInt(ProductVO::getProductRatingCount)
																.sum();
								store.setStoreRating(storeRating);
								store.setStoreRatingCount(storeRatingCount);
								storeRepository.save(store);

								//删除redis缓存
								String key = "StoreInfo" + store.getStoreId();
								redisTemplate.delete(key);
								redisTemplate.opsForHash().delete("AllStores", store.getStoreId() + "", store.toVO());
				}
				@Transactional
				public void updateReview(Product product) {
								List<Review> reviewList = reviewRepository.findByProductId(product.getProductId());
								double productRating = reviewList//商品评分
																.stream()
																.filter(ReviewVO -> ReviewVO.getRating()!=null)
																.mapToDouble(Review::getRating)
																.average()
																.orElse(0.0);
								productRating = Double.parseDouble(String.format("%.2f", productRating));//格式化评分为两位数
								int productCount = reviewList.size();//商品评分数量
								product.setProductRating(productRating);
								product.setProductRatingCount(productCount);
								productRepository.save(product);

								//删除redis缓存
								String key = "ProductInfo" + product.getProductId();
								redisTemplate.delete(key);
				}


}
