//package com.seecoder.BlueWhale.IntegrationTest;
//
//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import com.seecoder.BlueWhale.enums.CouponStateEnum;
//import com.seecoder.BlueWhale.enums.DeliveryEnum;
//import com.seecoder.BlueWhale.enums.OrderStateEnum;
//import com.seecoder.BlueWhale.enums.ProductTypeEnum;
//import com.seecoder.BlueWhale.exception.BlueWhaleException;
//import com.seecoder.BlueWhale.po.*;
//import com.seecoder.BlueWhale.repository.*;
//import com.seecoder.BlueWhale.service.OrderService;
//import com.seecoder.BlueWhale.vo.*;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//public class OrderIntegrationTest {
//
//				@Autowired
//				private OrderRepository orderRepository;
//
//				@Autowired
//				private ProductRepository productRepository;
//
//				@Autowired
//				private StoreRepository storeRepository;
//
//				@Autowired
//				private CouponRepository couponRepository;
//
//				@Autowired
//				private OrderService orderService;
//
//				private Store store;
//				private Product product;
//				private OrderVO orderVO;
//
//				@Before
//				public void setUp() {
//								// 清理现存数据
//								orderRepository.deleteAll();
//								productRepository.deleteAll();
//								storeRepository.deleteAll();
//								couponRepository.deleteAll();
//
//								// 初始化商店数据
//								store = new Store();
//								store.setName("Test Store");
//								storeRepository.save(store);
//
//								// 初始化商品数据
//								product = new Product();
//								product.setName("Test Product");
//								product.setPrice(100);
//								product.setInventory(10);
//								product.setStoreId(store.getStoreId());
//								productRepository.save(product);
//
//								// 初始化订单数据
//								orderVO = new OrderVO();
//								orderVO.setProductId(product.getProductId());
//								orderVO.setDeliveryOption(DeliveryEnum.DELIVERY);
//								orderVO.setQuantity(2);
//								orderVO.setUserId(1); // 假设用户ID为1
//				}
//
//				@After
//				public void tearDown() {
//								// 清理测试数据
//								orderRepository.deleteAll();
//								productRepository.deleteAll();
//								storeRepository.deleteAll();
//								couponRepository.deleteAll();
//				}
//
//				@Test
//				public void testOrder() {
//								// 创建订单
//								orderService.create(orderVO);
//
//								//销量增加
//								assertEquals(2, (int)storeRepository.findByName("Test Store").getSales());
//								assertEquals(2, (int)productRepository.findByProductId(product.getProductId()).getSales());
//
//								//库存不足
//								product.setInventory(1);
//								productRepository.save(product);
//								assertThrows(BlueWhaleException.class, ()->orderService.create(orderVO));
//
//
//								// 获取用户订单
//								List<OrderVO> userOrders = orderService.getUserOrders(orderVO.getUserId());
//								assertNotNull(userOrders);
//								assertEquals(1, userOrders.size());
//								assertEquals(orderVO.getProductId(), userOrders.get(0).getProductId());
//
//								// 更新订单状态错误
//								OrderVO updateOrderVO = new OrderVO();
//								updateOrderVO.setOrderId(createdOrder.getOrderId());
//								updateOrderVO.setState(OrderStateEnum.UNSEND);
//								assertThrows(BlueWhaleException.class, () -> orderService.updateInformation(updateOrderVO));
//
//								// 模拟支付成功
//								orderService.paySuccess(createdOrder.getOrderId(), 200.0, 0);
//								Order updatedOrder = orderRepository.findById(createdOrder.getOrderId()).orElse(null);
//								assertNotNull(updatedOrder);
//								assertEquals(OrderStateEnum.UNSEND, updatedOrder.getState());
//								assertEquals(200.0, updatedOrder.getPaid(), 0.01);
//
//								// 检查支付状态
//								assertTrue(orderService.checkPaySuccess(createdOrder.getOrderId()));
//
//								// 更新订单状态
//								OrderVO updateOrderVO1 = new OrderVO();
//								updateOrderVO1.setOrderId(createdOrder.getOrderId());
//								updateOrderVO1.setState(OrderStateEnum.UNGET);
//								assertTrue(orderService.updateInformation(updateOrderVO1));
//								updatedOrder = orderRepository.findById(createdOrder.getOrderId()).get();
//								assertEquals(OrderStateEnum.UNGET, updatedOrder.getState());
//
//								// 获取商店订单
//								List<OrderVO> storeOrders = orderService.getStoreOrders(store.getStoreId());
//								assertNotNull(storeOrders);
//								assertEquals(1, storeOrders.size());
//								assertEquals(orderVO.getProductId(), storeOrders.get(0).getProductId());
//
//								// 模拟退款成功
//								orderService.refundSuccess(createdOrder.getOrderId());
//								updatedOrder = orderRepository.findById(createdOrder.getOrderId()).orElse(null);
//								assertNotNull(updatedOrder);
//								assertEquals(OrderStateEnum.REFUND, updatedOrder.getState());
//				}
//}
