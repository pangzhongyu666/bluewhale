package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class CouponGroupVO {

				private Integer couponGroupId;

				private Integer storeId;

				private CouponTypeEnum type;

				private Double fillAmount;

				private Double reductionAmount;

				private Integer couponsAmount;//（剩余）总数

				private Integer couponGotAmount;//已被领取数目


				public CouponGroup toPO(){
								CouponGroup couponGroup = new CouponGroup();
								couponGroup.setCouponGroupId(this.couponGroupId);
								couponGroup.setType(this.type);
								couponGroup.setFillAmount(this.fillAmount);
								couponGroup.setReductionAmount(this.reductionAmount);
								couponGroup.setStoreId(this.storeId);
								couponGroup.setCouponsAmount(this.couponsAmount);
								couponGroup.setCouponGotAmount(this.couponGotAmount);
								return couponGroup;
				}

}
