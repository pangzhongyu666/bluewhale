package com.seecoder.BlueWhale.rabbitmq;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CouponListener {
				@Autowired
				CouponService couponService;

				private static final Logger logger = LoggerFactory.getLogger(CouponListener.class);

				@RabbitListener(bindings = @QueueBinding(
								value = @Queue(value = "coupon.claim.queue", durable = "true"),
								exchange = @Exchange(value = "coupon.exchange", durable = "true"),
								key = "coupon.claim"),
								errorHandler = "claimCouponErrorHandler"
				)
				public void claimCoupon(CouponVO couponVO) {
								//logger.info("RabbitMQ 收到优惠券领取请求");
								couponService.handleCoupon(couponVO);
				}

				@Bean
				public RabbitListenerErrorHandler claimCouponErrorHandler(RabbitTemplate rabbitTemplate) {
								return (amqpMessage, message, exception) -> {
												System.out.println("消息处理异常: " + exception.getMessage());

												Throwable cause = exception.getCause();

												if (cause instanceof BlueWhaleException &&(cause.getMessage().contains("优惠券已被领取") || cause.getMessage().contains("保存到数据库失败"))) {
																System.out.println("获取锁失败，立即重新入队");
																new RepublishMessageRecoverer(rabbitTemplate, "coupon.exchange", "coupon.claim").recover(amqpMessage, exception);
																return message;
												}

												if (cause instanceof RuntimeException) {
																System.out.println("临时性异常，立即重新入队");
																new ImmediateRequeueMessageRecoverer().recover(amqpMessage, exception);
																return message;
												}

												System.out.println("未知异常，重新入队");
												new RepublishMessageRecoverer(rabbitTemplate, "coupon.exchange", "coupon.claim").recover(amqpMessage, exception);
												return message;
								};
				}
}
