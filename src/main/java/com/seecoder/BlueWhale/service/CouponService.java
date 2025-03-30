package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.po.CouponGroup;
import com.seecoder.BlueWhale.vo.CouponGroupVO;
import com.seecoder.BlueWhale.vo.CouponVO;

import java.util.List;



public interface CouponService {
    Boolean createGroup(CouponGroupVO couponGroupVO);

    List<CouponVO> getUserCoupons(Integer userId);

    Double couponApply(Long orderId, Integer couponId);

    Boolean claimCoupon(Integer userId, Integer couponGroupId);
    Boolean checkCoupon(Integer userId, Integer couponGroupId);
    List<CouponGroupVO> getAllCouponsGroups();

    List<CouponGroupVO> getStoreCouponGroups(Integer storeId);

    CouponGroupVO getCouponGroupInfo(Integer couponGroupId);

    Boolean checkAndClaimCoupon(Integer userId, Integer couponGroupId);
}
