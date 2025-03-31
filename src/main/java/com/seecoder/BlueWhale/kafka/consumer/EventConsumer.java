package com.seecoder.BlueWhale.kafka.consumer;

import com.seecoder.BlueWhale.serviceImpl.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
				//采用监听方式接收数据、消息
				//@KafkaListener(topics = "kafka-test" , groupId = "group1")
				public void onEvent(String message) {
								System.out.println("接收到的信息  " + message);
				}
}
