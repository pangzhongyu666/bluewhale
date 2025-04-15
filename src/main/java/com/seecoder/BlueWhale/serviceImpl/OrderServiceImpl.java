package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.DeliveryEnum;
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
import com.seecoder.BlueWhale.util.RedisIdWorker;
import com.seecoder.BlueWhale.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

				@Autowired
				OrderRepository orderRepository;
				@Autowired
				ProductRepository productRepository;
				@Autowired
				StoreRepository storeRepository;
				@Autowired
				CouponRepository couponRepository;

				@Autowired
				RedisTemplate redisTemplate;
				@Autowired
				RabbitTemplate rabbitTemplate;

				@Autowired
				RedisIdWorker redisIdWorker;
				private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

				@Override
				@Transactional
				public Boolean create(OrderVO orderVO) {
								int quantity = orderVO.getQuantity();

								int inventory =  productRepository.deductInventory(orderVO.getProductId(), quantity);
								logger.info("库存剩余" + inventory);
								if(inventory == 0){
												throw BlueWhaleException.productInventoryShort();
								}

								//写入消息队列
								rabbitTemplate.convertAndSend("order.exchange", "order.create", orderVO);
								logger.info("订单写入消息队列");

								return true;
				}

				@Override
				@Transactional
				public void createOrder(OrderVO orderVO) {
								int quantity = orderVO.getQuantity();

								Product product = productRepository.findByProductId(orderVO.getProductId());
								product.setSales((product.getSales() != null ? product.getSales() : 0) + quantity);
								productRepository.save(product);
								redisTemplate.delete("ProductInfo" + product.getProductId());

								Store store = storeRepository.findByStoreId(product.getStoreId());
								int storeSales = store.getSales()!=null ? store.getSales()+ quantity: quantity;
								store.setSales(storeSales);//销量增加
								storeRepository.save(store);
								//删除redis缓存
								String key = "StoreInfo" + store.getStoreId();
								redisTemplate.delete(key);
								redisTemplate.opsForHash().delete("AllStores", store.getStoreId() + "");


								if(orderVO.getStoreId() == null){
												orderVO.setStoreId(product.getStoreId());
								}
								orderVO.setOrderId(redisIdWorker.nextId("order"));
								orderVO.setCreateTime(new Date());
								orderVO.setState(OrderStateEnum.UNPAID);
								Order order = orderVO.toPO();
								orderRepository.save(order);

								//发送消息到延迟队列
								rabbitTemplate.convertAndSend("order.delay.exchange", "order.delay", orderVO.getOrderId(), message -> {
												message.getMessageProperties().setDelay(1000000);//过期时间为10000秒
												return message;
								});
								logger.info("创建订单" + order.getOrderId() +"成功");
				}

				@Override
				public List<OrderVO> getUserOrders(Integer userId) {//用户订单界面
								return orderRepository.findByUserId(userId).stream().map(Order::toVO).collect(Collectors.toList());
				}
				@Override
				public List<OrderVO> getStoreOrders(Integer storeId) {//门店工作人员订单界面
								return orderRepository.findByStoreId(storeId).stream().map(Order::toVO).collect(Collectors.toList());
				}
				@Override
				public List<OrderVO> getProductOrders(Integer productId) {//门店工作人员订单界面
								return orderRepository.findByProductId(productId).stream().map(Order::toVO).collect(Collectors.toList());
				}
				@Override
				public List<OrderVO> getAllOrders() {//商场管理员与经理订单界面
								return orderRepository.findAll().stream().map(Order::toVO).collect(Collectors.toList());
				}

				@Override
				public Boolean updateInformation(OrderVO orderVO) {
								Order order = orderRepository.findById(orderVO.getOrderId()).orElse(null);
								if(order == null){
												throw BlueWhaleException.orderNotExists();
								}
								//UNSEND,UNGET,UNCOMMENT,DONE需按顺序更新
								if(orderVO.getState() != null){
												if(orderVO.getState() == OrderStateEnum.UNSEND){//只可通过pay来设置为UNSEND
																throw BlueWhaleException.orderStatusError();
												}else if(orderVO.getState() == OrderStateEnum.UNGET && order.getState() != OrderStateEnum.UNSEND){
																throw BlueWhaleException.orderStatusError();
												}else if(orderVO.getState() == OrderStateEnum.UNCOMMENT && order.getState() != OrderStateEnum.UNGET){
																throw BlueWhaleException.orderStatusError();
												}else if(orderVO.getState() == OrderStateEnum.DONE){
																if(order.getState() != OrderStateEnum.UNCOMMENT){
																				throw BlueWhaleException.orderStatusError();
																}else{
																				order.setFinishTime(new Date());//设置结束时间
																}
												}
												order.setState(orderVO.getState());
								}

								orderRepository.save(order);
								logger.info("更新订单"+ orderVO.getOrderId() + "状态为" + orderVO.getState());
								return true;
				}

				@Override
				public void paySuccess(Long orderId, Double paid, Integer couponId) {

								Order order = orderRepository.findById(orderId).get();
								if(order.getDeliveryOption() == DeliveryEnum.DELIVERY)
												order.setState(OrderStateEnum.UNSEND);//送货上门则变状态为未送货
								else
												order.setState(OrderStateEnum.UNGET);//到店取货则变状态为未取货
								order.setPaid(paid);


								if(couponId != 0){//0表示未使用优惠券
												Coupon coupon = couponRepository.findById(couponId).get();
												coupon.setState(CouponStateEnum.USED);//设置优惠券已使用
												couponRepository.save(coupon);
								}
								orderRepository.save(order);
								logger.info("支付成功，数据已存储");
				}

				@Override
				public void refundSuccess(Long orderId) {
								Order order = orderRepository.findById(orderId).get();
								order.setState(OrderStateEnum.REFUND);//订单状态设为退款

								int quantity = order.getQuantity();
								Product product =  productRepository.findByProductId(order.getProductId());
								int productSales = product.getSales() - quantity;
								product.setSales(productSales);//商品销量回调
								Store store = storeRepository.findByStoreId(product.getStoreId());
								int storeSales =  store.getSales() - quantity;
								store.setSales(storeSales);//商店销量回调

								orderRepository.save(order);
								logger.info("退款成功，数据已存储");
				}

				@Override
				public Boolean checkPaySuccess(Long orderId) {
								Order order = orderRepository.findById(orderId).orElse(null);
								if(order == null){
												throw BlueWhaleException.orderNotExists();
								}
								return order.getState() != OrderStateEnum.UNPAID
																&& order.getState() != OrderStateEnum.CANCELLED
																&& order.getState() != OrderStateEnum.REFUND;//不是未支付表示已支付
				}

//				@Override
//				@Scheduled(fixedRate = 60000) // 每分钟执行一次
//				public void cancelUnpaidOrders() {
//								logger.info("执行定时取消未支付订单任务");
//								List<Order> unpaidOrders = orderRepository.findByState(OrderStateEnum.UNPAID);
//								Date cutoffDate = new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000); // 14天前的时间
//
//								for (Order order : unpaidOrders) {
//												if(order.getCreateTime().before(cutoffDate)){
//																order.setState(OrderStateEnum.CANCELLED);
//
//																int quantity = order.getQuantity();
//																Product product =  productRepository.findByProductId(order.getProductId());
//																int productSales = product.getSales() - quantity;
//																product.setSales(productSales);//商品销量回调
//																Store store = storeRepository.findByStoreId(product.getStoreId());
//																int storeSales =  store.getSales() - quantity;
//																store.setSales(storeSales);//商店销量回调
//																logger.info("已取消过期订单" + order.getOrderId());
//
//																productRepository.save(product);
//																storeRepository.save(store);
//																orderRepository.save(order);
//												}
//								}
//				}

				@Override
				public void cancelUnpaidOrders(Long orderId) {
								Order order = orderRepository.findById(orderId).orElse(null);
								if(order == null || order.getState() != OrderStateEnum.UNPAID){
												throw new BlueWhaleException("订单不存在或者已支付");
								}
								
								//未支付
								order.setState(OrderStateEnum.CANCELLED);
								int quantity = order.getQuantity();
								Product product =  productRepository.findByProductId(order.getProductId());
								int productSales = product.getSales() - quantity;
								product.setSales(productSales);//商品销量回调
								product.setInventory(product.getInventory() + quantity);//商品库存回滚
								Store store = storeRepository.findByStoreId(product.getStoreId());
								int storeSales =  store.getSales() - quantity;
								store.setSales(storeSales);//商店销量回调
								logger.info("已取消过期订单" + order.getOrderId());
								productRepository.save(product);
								storeRepository.save(store);
								orderRepository.save(order);

				}
}
