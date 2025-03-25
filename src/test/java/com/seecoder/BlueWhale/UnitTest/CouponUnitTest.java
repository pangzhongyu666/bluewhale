package com.seecoder.BlueWhale.UnitTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.seecoder.BlueWhale.serviceImpl.CouponServiceImpl;
import com.seecoder.BlueWhale.vo.CouponGroupVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CouponUnitTest {

				@Mock
				private CouponRepository couponRepository;

				@Mock
				private StoreRepository storeRepository;

				@Mock
				private OrderRepository orderRepository;

				@Mock
				private CouponGroupRepository couponGroupRepository;

				@InjectMocks
				private CouponServiceImpl couponService;

				@BeforeEach
				public void setUp() {
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testCreateGroup() {
								CouponGroupVO couponGroupVO = new CouponGroupVO();
								couponGroupVO.setStoreId(1);
								Store store = new Store();
								store.setStoreId(1);

								when(storeRepository.findByStoreId(1)).thenReturn(store);

								Boolean result = couponService.createGroup(couponGroupVO);
								assertTrue(result);
								verify(couponGroupRepository, times(1)).save(any(CouponGroup.class));
				}

				@Test
				public void testGetUserCoupons() {
								Coupon coupon1 = new Coupon();
								coupon1.setCouponId(1);
								Coupon coupon2 = new Coupon();
								coupon2.setCouponId(2);

								when(couponRepository.findByUserId(1)).thenReturn(Arrays.asList(coupon1, coupon2));

								List<CouponVO> couponVOList = couponService.getUserCoupons(1);
								assertEquals(2, couponVOList.size());
								verify(couponRepository, times(1)).findByUserId(1);
				}

				@Test
				public void testGetAllCouponsGroups() {
								CouponGroup couponGroup1 = new CouponGroup();
								couponGroup1.setCouponGroupId(1);
								CouponGroup couponGroup2 = new CouponGroup();
								couponGroup2.setCouponGroupId(2);

								when(couponGroupRepository.findAll()).thenReturn(Arrays.asList(couponGroup1, couponGroup2));

								List<CouponGroupVO> couponGroupVOList = couponService.getAllCouponsGroups();
								assertEquals(2, couponGroupVOList.size());
								verify(couponGroupRepository, times(1)).findAll();
				}

				@Test
				public void testGetStoreCouponGroups() {
								CouponGroup couponGroup1 = new CouponGroup();
								couponGroup1.setCouponGroupId(1);
								CouponGroup couponGroup2 = new CouponGroup();
								couponGroup2.setCouponGroupId(2);

								when(couponGroupRepository.findByStoreId(1)).thenReturn(Arrays.asList(couponGroup1, couponGroup2));

								List<CouponGroupVO> couponGroupVOList = couponService.getStoreCouponGroups(1);
								assertEquals(2, couponGroupVOList.size());
								verify(couponGroupRepository, times(1)).findByStoreId(1);
				}

				@Test
				public void testGetCouponGroupInfo() {
								CouponGroup couponGroup = new CouponGroup();
								couponGroup.setCouponGroupId(1);

								when(couponGroupRepository.findById(1)).thenReturn(Optional.of(couponGroup));

								CouponGroupVO couponGroupVO = couponService.getCouponGroupInfo(1);
								assertEquals(1, couponGroupVO.getCouponGroupId());
								verify(couponGroupRepository, times(1)).findById(1);
				}

				@Test
				public void testCouponApply() {
								Order order = new Order();
								order.setOrderId(1);
								order.setPaid(100.0);
								order.setStoreId(1);

								Coupon coupon = new Coupon();
								coupon.setCouponId(1);
								coupon.setState(CouponStateEnum.AVAILABLE);
								coupon.setCouponGroupId(1);
								coupon.setStoreId(1);

								CouponGroup couponGroup = new CouponGroup();
								couponGroup.setCouponGroupId(1);
								couponGroup.setType(CouponTypeEnum.FILLREDUCTION);
								couponGroup.setFillAmount(50.0);
								couponGroup.setReductionAmount(10.0);

								when(orderRepository.findById(1)).thenReturn(Optional.of(order));
								when(couponRepository.findById(1)).thenReturn(Optional.of(coupon));
								when(couponGroupRepository.findById(1)).thenReturn(Optional.of(couponGroup));

								Double discount = couponService.couponApply(1, 1);
								assertEquals(90.0, discount);
								verify(orderRepository, times(1)).findById(1);
								verify(couponRepository, times(1)).findById(1);
								verify(couponGroupRepository, times(1)).findById(1);
				}

				@Test
				public void testClaimCoupon() {
								CouponGroup couponGroup = new CouponGroup();
								couponGroup.setCouponGroupId(1);
								couponGroup.setStoreId(1);
								couponGroup.setCouponsAmount(10);
								couponGroup.setCouponGotAmount(0);

								when(couponGroupRepository.findById(1)).thenReturn(Optional.of(couponGroup));

								Boolean result = couponService.claimCoupon(1, 1);
								assertTrue(result);
								assertEquals(9, couponGroup.getCouponsAmount());
								assertEquals(1, couponGroup.getCouponGotAmount());

								verify(couponRepository, times(1)).save(any(Coupon.class));
								verify(couponGroupRepository, times(1)).save(any(CouponGroup.class));
				}
}
