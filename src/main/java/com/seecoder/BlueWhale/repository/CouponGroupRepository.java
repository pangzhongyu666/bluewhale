package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponGroupRepository extends JpaRepository<CouponGroup, Integer> {

				List<CouponGroup> findByStoreId(Integer storeId);

				@Modifying
				@Query("UPDATE CouponGroup c SET c.couponsAmount = c.couponsAmount - 1 , c.couponGotAmount = c.couponGotAmount + 1 WHERE c.couponGroupId = :couponGroupId AND c.couponsAmount > 0")
				int deductStock(@Param("couponGroupId") Integer couponGroupId);
}
