package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.po.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ReviewVO {
				private Integer reviewId;

				private Double rating;

				private String comment;

				private List<String> reviewImages;

				private List<String> reviewImagesForRedis;

				private Integer productId;

				private Integer userId;

				private Integer parentId;

				public Review toPO(){
								Review review = new Review();
								review.setReviewId(this.reviewId);
								review.setRating(this.rating);
								review.setComment(this.comment);
								review.setProductId(this.productId);
								review.setUserId(this.userId);
								review.setReviewImages(this.getReviewImages());
								review.setReviewImagesForRedis(new ArrayList<>(this.getReviewImages()));
								review.setParentId(this.parentId);
								return  review;
				}

}
