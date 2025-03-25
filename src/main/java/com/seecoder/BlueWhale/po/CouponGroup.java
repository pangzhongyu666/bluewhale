package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.vo.CouponGroupVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coupon_group",
								indexes ={@Index(name = "idx_coupon_group_id", columnList = "coupon_group_id"),
																@Index(name = "idx_store_id", columnList = "store_id")}
)
public class CouponGroup {
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Id
				@Column(name = "coupon_group_id")
				private Integer couponGroupId;

				@Basic
				@Column(name = "store_id")
				private Integer storeId;//0表示全局

				@Basic
				@Column(name = "type")
				@Enumerated(EnumType.STRING)
				private CouponTypeEnum type;

				@Basic
				@Column(name = "fill_amount")
				private Double fillAmount;

				@Basic
				@Column(name = "reduction_amount")
				private Double reductionAmount;

				@Basic
				@Column(name = "coupons_amount")
				private Integer couponsAmount;//（剩余）总数

				@Basic
				@Column(name = "coupon_got_amount")
				private Integer couponGotAmount;//已被领取数目



				public CouponGroupVO toVO(){
								CouponGroupVO couponGroupVO = new CouponGroupVO();
								couponGroupVO.setCouponGroupId(this.couponGroupId);
								couponGroupVO.setType(this.type);
								couponGroupVO.setFillAmount(this.fillAmount);
								couponGroupVO.setReductionAmount(this.reductionAmount);
								couponGroupVO.setStoreId(this.storeId);
								couponGroupVO.setCouponsAmount(this.couponsAmount);
								couponGroupVO.setCouponGotAmount(this.couponGotAmount);
								return couponGroupVO;
				}
}
