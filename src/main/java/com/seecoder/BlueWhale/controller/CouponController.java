package com.seecoder.BlueWhale.controller;


import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    CouponService couponService;

    @PostMapping("/createGroup")
    public ResultVO<Boolean> createGroup(@RequestBody CouponGroupVO couponGroupVO) {
        return  ResultVO.buildSuccess(couponService.createGroup(couponGroupVO));
    }

    @GetMapping("/getUserCoupons/{userId}")
    public ResultVO<List<CouponVO>> getUserCoupons(@PathVariable(value="userId")Integer userId) {
               return ResultVO.buildSuccess(couponService.getUserCoupons(userId));
    }

    @GetMapping("/getStoreCouponGroups/{storeId}")
    public ResultVO<List<CouponGroupVO>> getStoreCouponGroups(@PathVariable(value="storeId")Integer storeId) {
        return ResultVO.buildSuccess(couponService.getStoreCouponGroups(storeId));
    }

    @GetMapping("/getAllCouponsGroups")
    public ResultVO<List<CouponGroupVO>> getAllCouponsGroups() {
        return ResultVO.buildSuccess(couponService.getAllCouponsGroups());
    }

    @GetMapping("/getCouponGroupInfo/{couponGroupId}")
    public ResultVO<CouponGroupVO> getCouponGroupInfo(@PathVariable(value="couponGroupId")Integer couponGroupId) {
        return ResultVO.buildSuccess(couponService.getCouponGroupInfo(couponGroupId));
    }

    @PostMapping("/CouponApply")
    public ResultVO<Double> couponApply(@RequestParam("orderId") Integer orderId, @RequestParam("couponId") Integer couponId)
    {
        return ResultVO.buildSuccess(couponService.couponApply(orderId,couponId));
    }

    @PostMapping("/claimCoupon")//领取优惠券
    public ResultVO<Boolean> claimCoupon(@RequestParam("userId") Integer userId, @RequestParam("couponGroupId") Integer couponGroupId) {
        return ResultVO.buildSuccess(couponService.claimCoupon(userId,couponGroupId));
    }
    @GetMapping("/checkCoupon/{userId}/{couponGroupId}")
    public ResultVO<Boolean> checkCoupon(@PathVariable("userId") Integer userId, @PathVariable("couponGroupId") Integer couponGroupId){
        return ResultVO.buildSuccess(couponService.checkCoupon(userId, couponGroupId));
    }
}
