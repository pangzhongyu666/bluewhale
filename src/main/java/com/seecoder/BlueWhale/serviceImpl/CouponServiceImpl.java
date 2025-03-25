package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponGroup;
import com.seecoder.BlueWhale.po.Order;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.CouponGroupRepository;
import com.seecoder.BlueWhale.repository.CouponRepository;
import com.seecoder.BlueWhale.repository.OrderRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.serviceImpl.strategy.Context;
import com.seecoder.BlueWhale.serviceImpl.strategy.FillReductionCouponCalculateStrategy;
import com.seecoder.BlueWhale.serviceImpl.strategy.SpecialCouponCalculateStrategy;
import com.seecoder.BlueWhale.vo.CouponGroupVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CouponGroupRepository couponGroupRepository;
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Override
    @CacheEvict(value = "couponsGroups", allEntries = true)
    public  Boolean createGroup(CouponGroupVO couponGroupVO) {
        Store store = storeRepository.findByStoreId(couponGroupVO.getStoreId());
        if(store == null && couponGroupVO.getStoreId() != 0){
            throw BlueWhaleException.storeNotExists();
        }
        CouponGroup newCouponGroup = couponGroupVO.toPO();
        couponGroupRepository.save(newCouponGroup);
        logger.info("创建优惠券组" + newCouponGroup.getCouponGroupId());
        return true;
    }

    @Override
    @Cacheable(value = "coupons", key = "#userId")
    public List<CouponVO> getUserCoupons(Integer userId) {//获取用户未使用的优惠券
        return couponRepository.findByUserId(userId).stream().filter(Coupon -> Coupon.getState() != CouponStateEnum.USED).map(Coupon::toVO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "couponsGroups")
    public List<CouponGroupVO> getAllCouponsGroups() {
        return couponGroupRepository.findAll().stream().map(CouponGroup::toVO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "couponsGroups")
    public List<CouponGroupVO> getStoreCouponGroups(Integer storeId) {
        return couponGroupRepository.findByStoreId(storeId).stream().map(CouponGroup::toVO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "couponsGroups")
    public CouponGroupVO getCouponGroupInfo(Integer couponGroupId) {
        return couponGroupRepository.findById(couponGroupId).get().toVO();
    }


    @Override
    @Cacheable(value = "price")
    public Double couponApply(Integer orderId, Integer couponId) {
        //每次选中优惠券就会调用这个方法计算优惠后价格
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            throw BlueWhaleException.orderNotExists();
        }
        Coupon coupon = couponRepository.findById(couponId).orElse(null);
        if(coupon == null){
            throw BlueWhaleException.couponNotExists();
        }
        if(coupon.getState() == CouponStateEnum.USED){//优惠券已使用
            throw BlueWhaleException.couponNotAvailable();
        }
        if(coupon.getStoreId() != 0 && coupon.getStoreId() != order.getStoreId()) {
            throw BlueWhaleException.couponNotAvailable();//优惠券使用范围不在该商店
        }
        CouponGroup couponGroup = couponGroupRepository.findById(coupon.getCouponGroupId()).get();

        Context context = null;//策略模式
        if(couponGroup.getType() == CouponTypeEnum.FILLREDUCTION ){
            context = new Context(new FillReductionCouponCalculateStrategy(couponGroup.getFillAmount(),couponGroup.getReductionAmount()));
        } else if (couponGroup.getType() == CouponTypeEnum.SPECIAL) {
            context = new Context(new SpecialCouponCalculateStrategy());
        }
        if(context == null){
            throw BlueWhaleException.couponNotAvailable();
        }
        logger.info("订单" + orderId + "应用优惠券" + couponId);
								return context.executeStrategy(order.getPaid());//计算价格后返回
    }

    @Override
    @Caching(evict ={
            @CacheEvict(value = "coupons", key = "#userId"),
            @CacheEvict(value = "couponsGroups",allEntries = true),
    })
    public Boolean claimCoupon(Integer userId, Integer couponGroupId) {
        List<CouponVO> couponVOList = couponRepository.findByCouponGroupId(couponGroupId).stream().map(Coupon::toVO).collect(Collectors.toList());
        for(CouponVO couponVO : couponVOList){
            if(Objects.equals(couponVO.getUserId(), userId)){//一个用户只能领取一张同一优惠券组的优惠券
                return false;//返回false表示领取失败
            }
        }
        CouponGroup couponGroup = couponGroupRepository.findById(couponGroupId).get();
        if(couponGroup.getCouponsAmount() <= 0){//优惠券是否充足
            throw BlueWhaleException.couponNotEnough();
        }
        couponGroup.setCouponsAmount(couponGroup.getCouponsAmount() - 1);//优惠券数目减少
        couponGroup.setCouponGotAmount(couponGroup.getCouponGotAmount() + 1);//优惠券获取数增加
        CouponVO couponVO = new CouponVO();
        couponVO.setCouponGroupId(couponGroupId);
        couponVO.setUserId(userId);
        couponVO.setStoreId(couponGroup.getStoreId());
        couponVO.setState(CouponStateEnum.AVAILABLE);
        couponRepository.save(couponVO.toPO());
        couponGroupRepository.save(couponGroup);
        logger.info("用户" + userId + "领取优惠券组" + couponGroupId + "中的优惠券");
        return true;
    }

    @Override
    public Boolean checkCoupon(Integer userId, Integer couponGroupId) {
        List<CouponVO> couponVOList = couponRepository.findByCouponGroupId(couponGroupId).stream().map(Coupon::toVO).collect(Collectors.toList());
        for(CouponVO couponVO : couponVOList){
            if(Objects.equals(couponVO.getUserId(), userId)){
                return false;//一个用户只能领取一张同一优惠券组的优惠券
            }
        }
        return true;
    }
}
