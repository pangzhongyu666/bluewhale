package com.seecoder.BlueWhale.exception;

import com.seecoder.BlueWhale.serviceImpl.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: DingXiaoyu
 * @Date: 0:26 2023/11/26
 * 你可以在这里自定义Exception
*/
public class BlueWhaleException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(BlueWhaleException.class);

				public BlueWhaleException(String message){
        super(message);
    }
    public static BlueWhaleException phoneAlreadyExists(){
        logger.error("手机号已经存在!");
        return new BlueWhaleException("手机号已经存在!");
    }

    public static BlueWhaleException notLogin(){
        logger.error("未登录!");
        return new BlueWhaleException("未登录!");
    }

    public static BlueWhaleException phoneOrPasswordError(){
        logger.error("手机号或密码错误!");
        return new BlueWhaleException("手机号或密码错误!");
    }

    public static BlueWhaleException fileUploadFail(){
        logger.error("文件上传失败!");
        return new BlueWhaleException("文件上传失败!");
    }

    public static BlueWhaleException storeNameAlreadyExists() {
        logger.error("商店名字已存在或为空!");
        return new BlueWhaleException("商店名字已存在或为空!");
    }
    public static BlueWhaleException storeNotExists() {
        logger.error("商店不存在");
        return new BlueWhaleException("商店不存在");
    }
    public static BlueWhaleException productNotExists() {
        logger.error("商品不存在");
        return new BlueWhaleException("商品不存在");
    }
    public static BlueWhaleException cartNotExists() {
        logger.error("购物车不存在");
        return new BlueWhaleException("购物车不存在");
    }
    public static BlueWhaleException productInventoryShort() {
        logger.error("商品库存不足");
        return new BlueWhaleException("商品库存不足");
    }
    public static BlueWhaleException orderNotExists(){
        logger.error("订单不存在！");
        return new BlueWhaleException("订单不存在！");
    }
    public static BlueWhaleException orderStatusError(){
        logger.error("订单状态错误！");
        return new BlueWhaleException("订单状态错误！");
    }
    public static BlueWhaleException couponNotExists(){
        logger.error("优惠券不存在！");
        return new BlueWhaleException("优惠券不存在！");
    }
    public static BlueWhaleException couponNotEnough(){
        logger.error("优惠券不足！");
        return new BlueWhaleException("优惠券不足！");
    }

    public static BlueWhaleException couponNotAvailable(){
        logger.error("优惠券不可用！");
        return new BlueWhaleException("优惠券不可用！");
    }
    public static BlueWhaleException payError(){
        logger.error("支付失败！");
        return new BlueWhaleException("支付失败！");
    }

    public static BlueWhaleException refundError(){
        logger.error("退款失败！");
        return new BlueWhaleException("退款失败！");
    }
    public static BlueWhaleException refundRefuse(){
        logger.error("不可退款！");
        return new BlueWhaleException("不可退款！");
    }

}
