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

				void paySuccess(Integer orderId, Double paid, Integer couponId);

				void refundSuccess(Integer orderId);

				Boolean checkPaySuccess(Integer orderId);
				void cancelUnpaidOrders();
}
