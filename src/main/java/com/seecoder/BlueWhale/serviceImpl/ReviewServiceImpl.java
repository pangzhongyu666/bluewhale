package com.seecoder.BlueWhale.serviceImpl;

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
import org.springframework.stereotype.Service;

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

				@Override
				public List<ReviewVO> getReviews(Integer productId) {//获取商品评论
								return reviewRepository.findByProductId(productId).stream().map(Review::toVO).collect(Collectors.toList());
				}

				@Override
				public List<ReviewVO> getChildrenReviews(Integer reviewId) {//获取子评论
								return reviewRepository.findByParentId(reviewId).stream().map(Review::toVO).collect(Collectors.toList());
				}

				@Override
				public Boolean createReview(ReviewVO reviewVO) {
								if(reviewVO.getParentId() == null){
												reviewVO.setParentId(0);//防止父评论id为空
								}
								Review review = reviewVO.toPO();
								reviewRepository.save(review);
								if(reviewVO.getParentId() != 0){//子评论不计入更新评分
												return true;
								}
								Product product = productRepository.findByProductId(reviewVO.getProductId());
								updateReview(product);
								Store store = storeRepository.findByStoreId(product.getStoreId());
								updateReview(store);
								logger.info("创建评论" + review.getReviewId());
								return true;
				}

				private void updateReview(Store store) {
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
				}

				private void updateReview(Product product) {
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
				}


}
