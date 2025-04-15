package com.seecoder.BlueWhale.RabbitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitSendTest {
				@Autowired
				private RabbitTemplate rabbitTemplate;

				@Test
				public void send() {
								rabbitTemplate.convertAndSend("work_queue", "hello world");
								System.out.println("发送消息成功");
				}

				@Test
				public void testWorkQueue() throws InterruptedException {
								for (int i = 1; i <= 100; i++) {
												rabbitTemplate.convertAndSend("work_queue", "hello " + i);
												Thread.sleep(10);
								}
								System.out.println("发送消息成功");
				}

				@Test
				public void testFanout(){
								rabbitTemplate.convertAndSend("bluewhale_fanout",null, "hello ");
								System.out.println("发送消息成功");
				}

				@Test
				public void testDirect() throws InterruptedException {
								rabbitTemplate.convertAndSend("bluewhale_direct","red", "red ");
								rabbitTemplate.convertAndSend("bluewhale_direct","yellow", "yellow ");
								rabbitTemplate.convertAndSend("bluewhale_direct","blue", "blue ");
				}

				@Test
				public void testTopic(){
								rabbitTemplate.convertAndSend("bluewhale_topic","china.news", "china.news ");
								//rabbitTemplate.convertAndSend("bluewhale_topic","japan.news", "japan.news ");
								//rabbitTemplate.convertAndSend("bluewhale_topic","china.weather", "china.weather ");
					   //rabbitTemplate.convertAndSend("bluewhale_topic","japan.weather", "japan.weather ");

				}
				@Test
				public void testObject() throws InterruptedException {
								Map<String, Object> msg = new HashMap<>();
								msg.put("name", "zhangsan");
								msg.put("age", 18);
								rabbitTemplate.convertAndSend("object_queue", msg);

				}

				@Test
				public void testConfirmCallback() throws InterruptedException {

								CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
								cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
												@Override
												public void onFailure(Throwable throwable) {
														System.out.println("消息发送失败：" + throwable.getMessage());
												}

												@Override
												public void onSuccess(CorrelationData.Confirm confirm) {
																if (confirm.isAck()) {
																				System.out.println("消息发送成功：" + confirm);
																}else{
																				System.out.println("消息发送失败：" + confirm);
																}
												}
								} );
								//路由成功，ack
								//rabbitTemplate.convertAndSend("bluewhale_direct","red", "red ", cd);
								//路由失败，return
								//rabbitTemplate.convertAndSend("bluewhale_direct","red1", "red ", cd);
								//发送失败，nack
								rabbitTemplate.convertAndSend("bluewhale_direct1","red", "red ", cd);

								Thread.sleep(1000);
				}

				@Test
				public void testPageOut(){
								Message message = MessageBuilder
																.withBody("hello world".getBytes(StandardCharsets.UTF_8))
																.setDeliveryMode(MessageDeliveryMode.PERSISTENT)
																.build();
								for (int i = 1; i <= 1000000; i++) {
												rabbitTemplate.convertAndSend("work_queue", message);
								}
				}

				@Test
				public void testLazyQueue(){
								Message message = MessageBuilder
																.withBody("hello world".getBytes(StandardCharsets.UTF_8))
																.setDeliveryMode(MessageDeliveryMode.PERSISTENT)
																.build();
								for (int i = 1; i <= 1000000; i++) {
												rabbitTemplate.convertAndSend("lazy_queue", message);
								}
				}
}
