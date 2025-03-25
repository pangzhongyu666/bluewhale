package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.ReviewService;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
				@Autowired
				ReviewService reviewService;

				@PostMapping("/createReview")
				public ResultVO<Boolean> createReview(@RequestBody ReviewVO reviewVO){
								return ResultVO.buildSuccess(reviewService.createReview(reviewVO));
				}
				@GetMapping("/getReviews/{productId}")
				public ResultVO<List<ReviewVO>> getReviews(@PathVariable(value="productId")Integer productId){
								return ResultVO.buildSuccess(reviewService.getReviews(productId));
				}

				@GetMapping("/getChildrenReviews/{reviewId}")
				public ResultVO<List<ReviewVO>> getChildrenReviews(@PathVariable(value="reviewId")Integer reviewId){
								return ResultVO.buildSuccess(reviewService.getChildrenReviews(reviewId));
				}

}
