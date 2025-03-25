package com.seecoder.BlueWhale.po;


import com.seecoder.BlueWhale.vo.ReviewVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "review",
								indexes ={@Index(name = "idx_review_id", columnList = "review_id"),
																@Index(name = "idx_product_id", columnList = "product_id"),
																@Index(name = "idx_parent_id", columnList = "parent_id")
								}
)
public class Review {
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Id
				@Column(name = "review_id")
				private Integer reviewId;

				@Basic
				@Column(name = "rating")
				private Double rating;

				@Basic
				@Column(name = "comment")
				private String comment;

				@ElementCollection
				@CollectionTable(name = "review_image_urls", joinColumns = @JoinColumn(name = "review_id"))
				private List<String> reviewImages;

				@Basic
				@Column(name = "product_id")
				private Integer productId;

				@Basic
				@Column(name = "user_id")
				private Integer userId;

				@Basic
				@Column(name = "parent_id")
				private Integer parentId;//parentId为0表示为父评论，为其他则为子评论，该id为其父评论id

				public ReviewVO toVO(){
								ReviewVO reviewVO = new ReviewVO();
								reviewVO.setReviewId(this.reviewId);
								reviewVO.setRating(this.rating);
								reviewVO.setComment(this.comment);
								reviewVO.setProductId(this.productId);
								reviewVO.setUserId(this.userId);
								reviewVO.setReviewImages(this.getReviewImages());
								reviewVO.setParentId(this.parentId);
								return  reviewVO;
				}


}
