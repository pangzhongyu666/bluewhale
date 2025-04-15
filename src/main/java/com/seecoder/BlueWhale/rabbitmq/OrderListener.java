package com.seecoder.BlueWhale.rabbitmq;

import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.vo.OrderVO;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
				@Autowired
				OrderService orderService;

				@RabbitListener(bindings = @QueueBinding(
								value = @Queue(value = "order.create.queue", durable = "true"),
								exchange = @Exchange(value = "order.exchange", durable = "true"),
								key = "order.create")
				)
				public void createOrder(OrderVO orderVO) {
								orderService.createOrder(orderVO);
				}

				@RabbitListener(bindings = @QueueBinding(
								value = @Queue(value = "order.delay.queue", durable = "true"),
								exchange = @Exchange(value = "order.delay.exchange", delayed = "true",durable = "true"),
								key = "order.delay")
				)
				public void cancelUnpaidOrder(Long orderId) {
								orderService.cancelUnpaidOrders(orderId);
				}
}
