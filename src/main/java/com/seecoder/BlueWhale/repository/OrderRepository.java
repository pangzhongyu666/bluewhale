package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.po.Order;
import com.seecoder.BlueWhale.vo.OrderVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

				List<Order> findByProductId(Integer productId);

				List<Order> findByUserId(Integer userId);

				List<Order> findByStoreId(Integer storeId);

				List<Order> findByState(OrderStateEnum state);

				Order findByTradeName(String tradeName);



}
