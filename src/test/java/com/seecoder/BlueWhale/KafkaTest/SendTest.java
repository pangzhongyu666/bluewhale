package com.seecoder.BlueWhale.KafkaTest;

import com.seecoder.BlueWhale.kafka.producer.EventProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) // 确保 JUnit 运行时加载 Spring 上下文
@SpringBootTest
public class SendTest {
				@Autowired
				EventProducer eventProducer;

				@Test
				public void testSendMessageWithStrStr(){
								eventProducer.sendMessage("kafka-test", "test");
				}

				@Test
				public void testSendMessageWithMessage(){
								eventProducer.sendMessage2();
				}

				@Test
				public void testSendMessageWithRecord(){
								eventProducer.sendMessage3();
				}

				@Test
				public void testSendMessageWithStrIntLongStrStr(){
								eventProducer.sendMessage4();
				}

				@Test
				public void testSendMessageDefault(){
								eventProducer.sendMessage5();
				}
}