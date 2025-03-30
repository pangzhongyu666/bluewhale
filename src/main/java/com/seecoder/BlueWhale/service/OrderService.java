package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.vo.OrderVO;

import java.util.List;

public interface OrderService {

				OrderVO create (OrderVO orderVO);

				List<OrderVO> getUserOrders(Integer userId);

				List<OrderVO> getStoreOrders(Integer storeId);

				List<OrderVO> getProductOrders(Integer productId);
				Boolean updateInformation(OrderVO orderVO);

				List<OrderVO> getAllOrders();

				void paySuccess(Long orderId, Double paid, Integer couponId);

				void refundSuccess(Long orderId);

				Boolean checkPaySuccess(Long orderId);
				void cancelUnpaidOrders();
}
