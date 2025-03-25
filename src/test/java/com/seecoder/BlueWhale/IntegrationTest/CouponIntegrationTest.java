package com.seecoder.BlueWhale.IntegrationTest;

import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.*;
import com.seecoder.BlueWhale.repository.*;
import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.CouponGroupVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CouponIntegrationTest {

				@Autowired
				private CouponRepository couponRepository;

				@Autowired
				private StoreRepository storeRepository;

				@Autowired
				private OrderRepository orderRepository;

				@Autowired
				private CouponGroupRepository couponGroupRepository;

				@Autowired
				private CouponService couponService;

				private Store store;
				private CouponGroupVO couponGroupVO1;
				private CouponGroupVO couponGroupVO2;
				private Coupon coupon;
				private Order order;

				@Before
				public void setUp() {
								// 清理现存数据
								couponRepository.deleteAll();
								couponGroupRepository.deleteAll();
								storeRepository.deleteAll();
								orderRepository.deleteAll();

								// 初始化商店数据
								store = new Store();
								store.setName("Test Store");
								storeRepository.save(store);

								// 初始化优惠券组数据
								couponGroupVO1 = new CouponGroupVO();
								couponGroupVO1.setStoreId(store.getStoreId());
								couponGroupVO1.setType(CouponTypeEnum.FILLREDUCTION);
								couponGroupVO1.setFillAmount(100.0);
								couponGroupVO1.setReductionAmount(20.0);
								couponGroupVO1.setCouponsAmount(10);
								couponGroupVO1.setCouponGotAmount(0);

								couponGroupVO2 = new CouponGroupVO();
								couponGroupVO2.setStoreId(store.getStoreId());
								couponGroupVO2.setType(CouponTypeEnum.SPECIAL);
								couponGroupVO2.setCouponsAmount(5);

								// 初始化订单数据
								order = new Order();
								order.setStoreId(store.getStoreId());
								order.setUserId(1); // 假设用户ID为1
								order.setPaid(150.0);
								orderRepository.save(order);
				}

				@After
				public void tearDown() {
								// 清理测试数据
								couponRepository.deleteAll();
								couponGroupRepository.deleteAll();
								storeRepository.deleteAll();
								orderRepository.deleteAll();
				}

				@Test
				public void testCouponService() {
								// 创建优惠券组
								Boolean createGroupResult = couponService.createGroup(couponGroupVO1);
								assertTrue(createGroupResult);
								createGroupResult = couponService.createGroup(couponGroupVO2);
								assertTrue(createGroupResult);

								// 获取所有优惠券组
								List<CouponGroupVO> allGroups = couponService.getAllCouponsGroups();
								CouponGroupVO couponGroup1 = allGroups.get(0);
								CouponGroupVO couponGroup2 = allGroups.get(1);
								assertEquals(2, allGroups.size());
								assertEquals(couponGroup1.getType(), CouponTypeEnum.FILLREDUCTION);
								assertEquals(couponGroup2.getType(), CouponTypeEnum.SPECIAL);
								int couponGroup1Id = couponGroup1.getCouponGroupId();

								// 获取商店优惠券组
								List<CouponGroupVO> storeGroups = couponService.getStoreCouponGroups(store.getStoreId());
								assertNotNull(storeGroups);
								assertEquals(2, storeGroups.size());

								// 获取优惠券组信息
								CouponGroupVO groupInfo = couponService.getCouponGroupInfo(couponGroup1Id);
								assertNotNull(groupInfo);
								assertEquals(groupInfo.getType(), CouponTypeEnum.FILLREDUCTION);
								assertEquals(groupInfo.getCouponsAmount(), couponGroupVO1.getCouponsAmount());
								// 尝试获取不存在的优惠券组信息
								assertThrows(Exception.class, () -> couponService.getCouponGroupInfo(999));

								// 领取优惠券
								Boolean claimResult = couponService.claimCoupon(1, couponGroup1Id);
								couponGroup1 = couponGroupRepository.findById(couponGroup1Id).get().toVO();
								assertEquals(9, (int)couponGroup1.getCouponsAmount());
								assertEquals(1, (int)couponGroup1.getCouponGotAmount());
								assertTrue(claimResult);
								// 领取同一优惠券组的优惠券时，用户只能领取一张
								assertFalse(couponService.claimCoupon(1, couponGroup1.getCouponGroupId()));
								// 领取优惠券时优惠券不足
								couponGroup1.setCouponsAmount(0);
								couponGroupRepository.save(couponGroup1.toPO());
								assertThrows(BlueWhaleException.class, () -> couponService.claimCoupon(2, couponGroup1Id));


								// 获取用户优惠券
								List<CouponVO> userCoupons = couponService.getUserCoupons(1);
								assertNotNull(userCoupons);
								assertEquals(1, userCoupons.size());
								Coupon coupon = userCoupons.get(0).toPO();
								assertEquals(CouponStateEnum.AVAILABLE, coupon.getState());
								assertEquals(couponGroup1.getCouponGroupId(), coupon.getCouponGroupId());

								// 应用优惠券
								Double discount = couponService.couponApply(order.getOrderId(), coupon.getCouponId());
								assertEquals(130.0, discount, 0.01);


								// 应用无效优惠券
								coupon.setState(CouponStateEnum.USED);
								couponRepository.save(coupon);
								assertThrows(BlueWhaleException.class, () -> couponService.couponApply(order.getOrderId(), coupon.getCouponId()));

								// 应用不属于当前商店的优惠券
								coupon.setState(CouponStateEnum.AVAILABLE);
								coupon.setStoreId(999); // 假设一个不同的商店ID
								couponRepository.save(coupon);
								assertThrows(BlueWhaleException.class, () -> couponService.couponApply(order.getOrderId(), coupon.getCouponId()));

				}
}
