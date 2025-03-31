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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    //private BlockingQueue<CouponVO> couponTask = new ArrayBlockingQueue<>(1000 * 1000);
    private static final ExecutorService couponClaimExecutor = Executors.newSingleThreadExecutor();
    @PostConstruct
    private void init(){
        couponClaimExecutor.submit(new CouponClaimTask());
    }

    //lua脚本
    private static final DefaultRedisScript<Long> CHECKSTOCKANDGOT_SCRIPT;
    static {
        CHECKSTOCKANDGOT_SCRIPT = new DefaultRedisScript<>();
        CHECKSTOCKANDGOT_SCRIPT.setLocation(new ClassPathResource("couponGroup.lua"));
        CHECKSTOCKANDGOT_SCRIPT.setResultType(Long.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    private CouponService proxy;

    @Override
    public  Boolean createGroup(CouponGroupVO couponGroupVO) {
        Store store = storeRepository.findByStoreId(couponGroupVO.getStoreId());
        if(store == null && couponGroupVO.getStoreId() != 0){
            throw BlueWhaleException.storeNotExists();
        }
        CouponGroup newCouponGroup = couponGroupVO.toPO();
        couponGroupRepository.save(newCouponGroup);
        //redis缓存优惠券组剩余数量
        String redisKey = "CGCouponsAmount:\"" + newCouponGroup.getCouponGroupId() + "\"";
        stringRedisTemplate.opsForValue().set(redisKey, newCouponGroup.getCouponsAmount().toString());
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

//    @Override
//    public Boolean claimCoupon(Integer userId, Integer couponGroupId) {
//        //执行lua脚本
//        Long res = (Long) redisTemplate.execute(
//                CHECKSTOCKANDGOT_SCRIPT,
//                Collections.emptyList(),
//                couponGroupId.toString(),
//                userId.toString()
//        );
//        int r = res.intValue();
//        if (r == 0) {
//            //有购买资格，将信息存入消息队列
//
//            CouponVO couponVO = new CouponVO();
//            couponVO.setCouponGroupId(couponGroupId);
//            couponVO.setUserId(userId);
//            couponVO.setState(CouponStateEnum.AVAILABLE);
//            couponTask.add(couponVO);
//
//            proxy = (CouponService)AopContext.currentProxy();
//
//            return true;
//        } else {
//            throw new BlueWhaleException(r == 1 ? "优惠券组库存不足" : "用户已经领取过该优惠券组");
//        }
//    }
    @Override
    public Boolean claimCoupon(Integer userId, Integer couponGroupId) {
        //执行lua脚本
        Long res = (Long) redisTemplate.execute(
                CHECKSTOCKANDGOT_SCRIPT,
                Collections.emptyList(),
                couponGroupId.toString(),
                userId.toString()
        );
        int r = res.intValue();
        if (r == 0) {
            proxy = (CouponService)AopContext.currentProxy();
            return true;
        } else {
            throw new BlueWhaleException(r == 1 ? "优惠券组库存不足" : "用户已经领取过该优惠券组");
        }
				}
    private class CouponClaimTask implements Runnable {
        @Override
        public void run() {
            while (true){
																try {
																				//CouponVO coupon = couponTask.take();
                    //获取消息队列 XREADGROUP group g1 c1 count 1 block 2000 streams stream.coupon >
                    List<MapRecord<String, Object, Object>> list
                     = redisTemplate.opsForStream() .read(Consumer.from("g1", "c1"),
                                    StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                                    StreamOffset.create("stream.coupon", ReadOffset.lastConsumed()));
                    //判断获取消息是否成功
                    if(list == null || list.isEmpty()){
                        //获取消息队列失败
                        continue;
                    }
                    //解析消息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    CouponVO couponVO = new CouponVO();
                    couponVO.setCouponGroupId(Integer.valueOf(value.get("couponGroupId").toString()));
                    couponVO.setUserId(Integer.valueOf(value.get("userId").toString()));
                    couponVO.setState(CouponStateEnum.AVAILABLE);
                    //获取消息成功，执行任务
                    handleCoupon(couponVO);
                    //ACK确认 SACK stream.coupon g1 id
                    redisTemplate.opsForStream().acknowledge("stream.coupon", "g1", record.getId());
																} catch (Exception e) {
                    logger.error("获取优惠券消息失败", e);
                    handlePendingList();
																}
            }
        }
        private void handlePendingList() {
            while (true){
                try {
                    //CouponVO coupon = couponTask.take();
                    //获取pending list XREADGROUP group g1 c1 count 1 streams stream.coupon >
                    List<MapRecord<String, Object, Object>> list
                            = redisTemplate.opsForStream() .read(Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create("stream.coupon", ReadOffset.from("0")));
                    //判断获取消息是否成功
                    if(list == null || list.isEmpty()){
                        //获取失败, 说明pending list没有消息，直接返回
                        break;
                    }
                    //解析消息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    CouponVO couponVO = new CouponVO();
                    couponVO.setCouponGroupId(Integer.valueOf(value.get("couponGroupId").toString()));
                    couponVO.setUserId(Integer.valueOf(value.get("userId").toString()));
                    couponVO.setState(CouponStateEnum.AVAILABLE);
                    //获取消息成功，执行任务
                    handleCoupon(couponVO);
                    //ACK确认 SACK stream.coupon g1 id
                    redisTemplate.opsForStream().acknowledge("stream.coupon", "g1", record.getId());
                } catch (Exception e) {
                    logger.error("获取优惠券消息失败", e);
                }
            }
        }
    }

//    @Override
//    public Boolean claimCoupon(Integer userId, Integer couponGroupId) {
//        //Integer couponsAmount = couponGroupRepository.findById(couponGroupId).get().getCouponsAmount();
//        //if(couponsAmount < 1){
//        //    throw new BlueWhaleException("优惠券组库存不足");
//        //}
//        //锁一个用户，防止一个用户并发抢券
//        // intern()是为了找常量池中的对象，避免每次都创建新对象
//
//        //先加锁再进入事务，提交事务后锁才会释放
//        //这个锁在集群环境下是无效的，因为锁是基于进程的，JVM常量池是基于进程的，所以需要使用分布式锁
//        //synchronized (userId.toString().intern()) {
//        //    //自我调用，会导致事务失效
//        //    //解决方法：使用AopContext.currentProxy()获取代理对象，然后调用代理对象的方法
//        //    //这样就可以保证事务生效了
//        //    CouponService proxy = (CouponService)AopContext.currentProxy();
//        //    return proxy.checkAndClaimCoupon(userId, couponGroupId);
//        //}
//
//        //redis分布式锁
//        //RedisLock lock = new RedisLock("Coupon:" + userId, redisTemplate);
//
//        //redisson锁
//        RLock lock = redissonClient.getLock("Coupon:" + userId);
//        boolean lockSuccess = lock.tryLock();
//
//        if(!lockSuccess){
//            throw new BlueWhaleException("获取锁失败，一个用户只能领取一张同一优惠券组的优惠券");
//        }
//        try {
//            logger.info("获取锁成功");
//            CouponService proxy = (CouponService)AopContext.currentProxy();
//            return proxy.checkAndClaimCoupon(userId, couponGroupId);
//        } catch (IllegalStateException e) {
//            throw new RuntimeException(e);
//        } finally {
//            logger.info("释放锁");
//            lock.unlock();
//        }
//
//    }

    public void handleCoupon(CouponVO couponVO) {
								//redis分布式锁
        //RedisLock lock = new RedisLock("Coupon:" + userId, redisTemplate);

        //redisson锁
        RLock lock = redissonClient.getLock("Coupon:" + couponVO.getUserId());
        boolean lockSuccess = lock.tryLock();

        if(!lockSuccess){
            throw new BlueWhaleException("获取锁失败，一个用户只能领取一张同一优惠券组的优惠券");
        }
        try {
            logger.info("获取锁成功");
            proxy.checkAndClaimCoupon(couponVO);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } finally {
            logger.info("释放锁");
            lock.unlock();
        }

    }
    @Transactional
    public Boolean checkAndClaimCoupon(CouponVO couponVO) {
        int userId = couponVO.getUserId();
        checkCoupon(userId, couponVO.getCouponGroupId());
        int couponGroupId = couponVO.getCouponGroupId();
        int couponAmount = couponGroupRepository.deductStock(couponGroupId);
        if(couponAmount == 0){
            logger.info("优惠券组" + couponGroupId + "库存不足");
            throw new BlueWhaleException("优惠券不足");
        }
        CouponGroup couponGroup = couponGroupRepository.findById(couponGroupId).get();
        couponVO.setStoreId(couponGroup.getStoreId());
        couponRepository.save(couponVO.toPO());
        logger.info("用户" + userId + "领取优惠券组" + couponGroupId + "中的优惠券");
        return true;
    }

    @Override
    public Boolean checkCoupon(Integer userId, Integer couponGroupId) {
        return couponRepository.existsByCouponGroupIdAndUserId(couponGroupId, userId);
    }
}
