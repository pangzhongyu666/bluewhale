package com.seecoder.BlueWhale.configure;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfirmConfig implements ApplicationContextAware {



				@Override
				public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
								RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
								rabbitTemplate.setReturnCallback( new RabbitTemplate.ReturnCallback() {
												@Override
												public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
																System.out.println("消息：" + message + " 被服务器退回，退回原因：" + replyText + "， 交换机是：" + exchange + "  路由 key ：" + routingKey);
												}
								}

								);
				}
}
