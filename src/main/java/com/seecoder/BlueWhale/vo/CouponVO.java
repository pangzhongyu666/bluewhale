package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.service.CouponService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.criteria.CriteriaBuilder;

@Getter
@Setter
@NoArgsConstructor
public class CouponVO {
    private Integer couponId;

    private CouponStateEnum state;//优惠券状态：不可用or可用or已经使用

    private Integer userId;

    private Integer storeId;//0表示全局

    private Integer couponGroupId;


    public Coupon toPO() {
        Coupon coupon = new Coupon();
        coupon.setCouponId(this.couponId);
        coupon.setUserId(this.userId);
        coupon.setState(this.state);
        coupon.setStoreId(this.storeId);
        coupon.setCouponGroupId(this.couponGroupId);
        return coupon;
    }
}
