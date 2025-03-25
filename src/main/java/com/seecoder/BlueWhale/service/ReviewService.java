package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.vo.ReviewVO;

import java.util.List;

public interface ReviewService {
				Boolean createReview(ReviewVO reviewVO);
				List<ReviewVO> getReviews(Integer productId);

				List<ReviewVO> getChildrenReviews(Integer reviewId);
}
