package com.seecoder.BlueWhale.UnitTest;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.*;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.serviceImpl.OrderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.Order;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.CouponRepository;
import com.seecoder.BlueWhale.repository.OrderRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.vo.OrderVO;

public class OrderUnitTest {

				@Mock
				private OrderRepository orderRepository;

				@Mock
				private ProductRepository productRepository;

				@Mock
				private StoreRepository storeRepository;

				@Mock
				private CouponRepository couponRepository;

				@InjectMocks
				private OrderServiceImpl orderService;

				@Before
				public void setUp() {
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testCreateOrder() {
								OrderVO orderVO = new OrderVO();
								orderVO.setProductId(1);
								orderVO.setQuantity(2);

								Product product = new Product();
								product.setProductId(1);
								product.setInventory(5);
								product.setSales(10);
								product.setStoreId(1);

								Store store = new Store();
								store.setStoreId(1);
								store.setSales(20);

								when(productRepository.findByProductId(orderVO.getProductId())).thenReturn(product);
								when(storeRepository.findByStoreId(product.getStoreId())).thenReturn(store);

//								OrderVO newOrderVO = orderService.create(orderVO);
//								assertNotNull(newOrderVO);
//								assertNotNull(newOrderVO.getCreateTime());
//								assertEquals(newOrderVO.getStoreId(), store.getStoreId());


								assertEquals(3, (int)product.getInventory());
								assertEquals(12, (int)product.getSales());
								assertEquals(22, (int)store.getSales());
								verify(productRepository, times(1)).save(product);
								verify(storeRepository, times(1)).save(store);
								verify(orderRepository, times(1)).save(any(Order.class));
				}

				@Test
				public void testUpdateOrderInformation() {
								Order order = new Order();
								order.setOrderId(1L);
								order.setState(OrderStateEnum.UNSEND);

								OrderVO orderVO = new OrderVO();
								orderVO.setOrderId(1L);
								orderVO.setState(OrderStateEnum.UNGET);

								when(orderRepository.findById(orderVO.getOrderId())).thenReturn(Optional.of(order));

								orderService.updateInformation(orderVO);

								assertEquals(OrderStateEnum.UNGET, order.getState());
								verify(orderRepository, times(1)).save(order);
				}
				@Test
				public void testGetUserOrders() {
								Order order1 = new Order();
								order1.setOrderId(1L);
								Order order2 = new Order();
								order2.setOrderId(2L);

								when(orderRepository.findByUserId(1)).thenReturn(Arrays.asList(order1, order2));

								List<OrderVO> orderVOList = orderService.getUserOrders(1);
								assertEquals(2, orderVOList.size());
								verify(orderRepository, times(1)).findByUserId(1);
				}
				@Test
				public void testGetStoreOrders() {
								Order order1 = new Order();
								order1.setOrderId(1L);
								Order order2 = new Order();
								order2.setOrderId(2L);

								when(orderRepository.findByStoreId(1)).thenReturn(Arrays.asList(order1, order2));

								List<OrderVO> orderVOList = orderService.getStoreOrders(1);
								assertEquals(2, orderVOList.size());
								verify(orderRepository, times(1)).findByStoreId(1);
				}

				@Test
				public void testGetProductOrders() {
								Order order1 = new Order();
								order1.setOrderId(1L);
								Order order2 = new Order();
								order2.setOrderId(2L);

								when(orderRepository.findByProductId(1)).thenReturn(Arrays.asList(order1, order2));

								List<OrderVO> orderVOList = orderService.getProductOrders(1);
								assertEquals(2, orderVOList.size());
								verify(orderRepository, times(1)).findByProductId(1);
				}

				@Test
				public void testGetAllOrders() {
								Order order1 = new Order();
								order1.setOrderId(1L);
								Order order2 = new Order();
								order2.setOrderId(2L);

								when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

								List<OrderVO> orderVOList = orderService.getAllOrders();
								assertEquals(2, orderVOList.size());
								verify(orderRepository, times(1)).findAll();
				}

				@Test
				public void testCheckPaySuccess() {
								Order order = new Order();
								order.setOrderId(1L);
								order.setState(OrderStateEnum.UNSEND);

								when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

								Boolean isPaid = orderService.checkPaySuccess(1L);
								assertTrue(isPaid);
								verify(orderRepository, times(1)).findById(1L);
				}

				@Test(expected = BlueWhaleException.class)
				public void testCheckPaySuccess_OrderNotExists() {
								when(orderRepository.findById(1L)).thenReturn(Optional.empty());

								orderService.checkPaySuccess(1L);

								verify(orderRepository, times(1)).findById(1L);
				}
				@Test
				public void testPaySuccess() {
								Order order = new Order();
								order.setOrderId(1L);
								order.setDeliveryOption(DeliveryEnum.DELIVERY);

								Coupon coupon = new Coupon();
								coupon.setCouponId(1);

								when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
								when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

								orderService.paySuccess(order.getOrderId(), 100.0, coupon.getCouponId());

								assertEquals(OrderStateEnum.UNSEND, order.getState());
								assertEquals(100.0, order.getPaid(), 0.0);
								assertEquals(CouponStateEnum.USED, coupon.getState());
								verify(orderRepository, times(1)).save(order);
								verify(couponRepository, times(1)).save(coupon);
				}

				@Test
				public void testRefundSuccess() {
								Order order = new Order();
								order.setOrderId(1L);
								order.setProductId(1);
								order.setQuantity(2);

								Product product = new Product();
								product.setProductId(1);
								product.setInventory(5);
								product.setSales(10);
								product.setStoreId(1);

								Store store = new Store();
								store.setStoreId(1);
								store.setSales(20);

								when(productRepository.findByProductId(order.getProductId())).thenReturn(product);
								when(storeRepository.findByStoreId(product.getStoreId())).thenReturn(store);
								when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

								orderService.refundSuccess(order.getOrderId());

								assertEquals(OrderStateEnum.REFUND, order.getState());
								verify(orderRepository, times(1)).save(order);
				}

				@Test
				public void testCancelUnpaidOrders() {
								Order order = new Order();
								order.setOrderId(1L);
								order.setState(OrderStateEnum.UNPAID);
								order.setCreateTime(new Date(System.currentTimeMillis() - 15L * 24 * 60 * 60 * 1000)); // 15 days ago
								order.setProductId(1);
								order.setQuantity(2);

								Product product = new Product();
								product.setProductId(1);
								product.setInventory(5);
								product.setSales(10);
								product.setStoreId(1);

								Store store = new Store();
								store.setStoreId(1);
								store.setSales(20);

								List<Order> unpaidOrders = new ArrayList<>();
								unpaidOrders.add(order);

								when(orderRepository.findByState(OrderStateEnum.UNPAID)).thenReturn(unpaidOrders);
								when(productRepository.findByProductId(order.getProductId())).thenReturn(product);
								when(storeRepository.findByStoreId(product.getStoreId())).thenReturn(store);

								orderService.cancelUnpaidOrders();

								assertEquals(OrderStateEnum.CANCELLED, order.getState());
								verify(orderRepository, times(1)).save(order);
				}
}
