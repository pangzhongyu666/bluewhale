package com.seecoder.BlueWhale.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Component
public class EventProducer {
				//加入了spring-kafka依赖，自动装配好了KafkaTemplate这个Bean,可以直接使用
				@Autowired
				private KafkaTemplate<String, String> kafkaTemplate;

				public void sendMessage(String topic, String message) {
								kafkaTemplate.send(topic, message);
				}

				public void sendMessage2() {
								//通过构建器模式创建
								Message<String> message = MessageBuilder
																.withPayload("Hello, Kafka!")
																.setHeader(KafkaHeaders.TOPIC, "kafka-test")
																.build();
								kafkaTemplate.send(message);
				}

				public void sendMessage3() {
								//String topic, Integer partition, Long timestamp, K key, V value, Iterable<Header> headers

								//Headers 里面放的是一些信息，消息是key-value形式的，到时候消费者可以获得其中的信息
								Headers headers = new RecordHeaders();
								headers.add("phone", "18898663386".getBytes(StandardCharsets.UTF_8));
								headers.add("orderId", "1".getBytes());
								ProducerRecord<String, String> record = new ProducerRecord<>(
																"kafka-test",
																0,
																System.currentTimeMillis(),
																"key1",
																"Hello, Kafka!",
																headers
								);
								kafkaTemplate.send(record);
				}

				public void sendMessage4() {
								//string topic, Integer partition, Long timestamp, K key, V value
								kafkaTemplate.send("kafka-test", 0, System.currentTimeMillis(), "key2", "Hello, Kafka!");
				}

				public void sendMessage5() {
								//Integer partition, Long timestamp, K key, V value
								kafkaTemplate.sendDefault(0, System.currentTimeMillis(), "key3", "Hello, Kafka!");
				}

}
