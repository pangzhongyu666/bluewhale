package com.seecoder.BlueWhale.configure;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener.simple.retry", name = "enabled", havingValue = "true")
public class MqErrorConfig {
				@Bean
				public DirectExchange directExchange() {
								return new DirectExchange("error.direct");
				}

				@Bean
				public Queue queue() {
								return new Queue("error.queue");
				}

				@Bean
				public Binding binding() {
								return BindingBuilder.bind(queue()).to(directExchange()).with("error");
				}

				/**
					* 定义默认的消息恢复策略，发送到错误交换机
					*/
				@Bean
				public MessageRecoverer recoverer(RabbitTemplate rabbitTemplate) {
								return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
				}

				/**
					* 优惠券领取错误处理器：根据异常类型决定不同的恢复策略
					*/
}
