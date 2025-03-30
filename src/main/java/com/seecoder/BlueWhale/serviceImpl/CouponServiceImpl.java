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
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<CouponVO> getUserCoupons(Integer userId) {//获取用户未使用的优惠券
        return couponRepository.findByUserId(userId).stream().filter(Coupon -> Coupon.getState() != CouponStateEnum.USED).map(Coupon::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CouponGroupVO> getAllCouponsGroups() {
        return couponGroupRepository.findAll().stream().map(CouponGroup::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CouponGroupVO> getStoreCouponGroups(Integer storeId) {
        return couponGroupRepository.findByStoreId(storeId).stream().map(CouponGroup::toVO).collect(Collectors.toList());
    }

    @Override
    public CouponGroupVO getCouponGroupInfo(Integer couponGroupId) {
        return couponGroupRepository.findById(couponGroupId).get().toVO();
    }


    @Override
    public Double couponApply(Long orderId, Integer couponId) {
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
    public Boolean claimCoupon(Integer userId, Integer couponGroupId) {
        //锁一个用户，防止一个用户并发抢券
        // intern()是为了找常量池中的对象，避免每次都创建新对象

        //先加锁再进入事务，提交事务后锁才会释放
        //这个锁在集群环境下是无效的，因为锁是基于进程的，JVM常量池是基于进程的，所以需要使用分布式锁
        synchronized (userId.toString().intern()) {
            //自我调用，会导致事务失效
            //解决方法：使用AopContext.currentProxy()获取代理对象，然后调用代理对象的方法
            //这样就可以保证事务生效了
            CouponService proxy = (CouponService)AopContext.currentProxy();
            return proxy.checkAndClaimCoupon(userId, couponGroupId);
        }
    }
    @Transactional
    public Boolean checkAndClaimCoupon(Integer userId, Integer couponGroupId) {
        if(checkCoupon(userId, couponGroupId)){
            logger.info("用户" + userId + "已经领取过优惠券组" + couponGroupId);
            return false;//一个用户只能领取一张同一优惠券组的优惠券
        }

        int couponAmount = couponGroupRepository.deductStock(couponGroupId);

        if(couponAmount == 0){
            logger.info("优惠券组" + couponGroupId + "库存不足");
            throw new BlueWhaleException("优惠券不足");
        }
        CouponGroup couponGroup = couponGroupRepository.findById(couponGroupId).get();
        CouponVO couponVO = new CouponVO();
        couponVO.setCouponGroupId(couponGroupId);
        couponVO.setUserId(userId);
        couponVO.setStoreId(couponGroup.getStoreId());
        couponVO.setState(CouponStateEnum.AVAILABLE);
        couponRepository.save(couponVO.toPO());
        logger.info("用户" + userId + "领取优惠券组" + couponGroupId + "中的优惠券");
        return true;
    }

    @Override
    public Boolean checkCoupon(Integer userId, Integer couponGroupId) {
        return couponRepository.existsByCouponGroupIdAndUserId(couponGroupId, userId);
    }
}
