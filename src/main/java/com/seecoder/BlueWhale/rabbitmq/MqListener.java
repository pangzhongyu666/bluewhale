package com.seecoder.BlueWhale.rabbitmq;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MqListener {
				@RabbitListener(queues = "work_queue")
				public void receiveMessage1(String message) throws InterruptedException {
								System.out.println("消费者1 | " + "Received: " + message);
								throw new RuntimeException("测试异常");
				}

				//@RabbitListener(queues = "work_queue")
				public void receiveMessage2(String message) throws InterruptedException {
								System.out.println("消费者2 | "+ "Received: " + message);
								Thread.sleep(200);
				}


				@RabbitListener(queues = "fanout_queue1")
				public void receiveMessage11(String message) {
								System.out.println("消费者1 | " + "fanout_queue1: " + message);
				}

				@RabbitListener(queues = "fanout_queue2")
				public void receiveMessage22(String message)  {
								System.out.println("消费者2 | "+ "fanout_queue2: " + message);
				}

				@RabbitListener(queues = "direct_queue1")
				public void receiveMessage111(String message) {
								System.out.println("消费者1 | " + "direct_queue1: " + message);
				}

				@RabbitListener(queues = "direct_queue2")
				public void receiveMessage222(String message)  {
								System.out.println("消费者2 | "+ "direct_queue2: " + message);
				}

				@RabbitListener(bindings = @QueueBinding(
												value = @Queue(value = "topic_queue1", durable = "true"),
												exchange = @Exchange(name = "bluewhale_topic", durable = "true", type = "topic"),
												key = "china.#")
				)
				public void receiveMessage1111(String message) {
								System.out.println("消费者1 | " + "topic_queue1: " + message);
				}

				@RabbitListener(bindings = @QueueBinding(
												value = @Queue(value = "topic_queue2", durable = "true"),
												exchange = @Exchange(name = "bluewhale_topic", durable = "true", type = "topic"),
												key = "#.news")
				)
				public void receiveMessage2222(String message)  {
								System.out.println("消费者2 | "+ "topic_queue2: " + message);
				}

				@RabbitListener(bindings = @QueueBinding(
								value = @Queue(value = "topic_queue3", durable = "true"),
								exchange = @Exchange(name = "bluewhale_topic", durable = "true", type = "topic"),
								key = "china.#")
				)
				public void receiveMessage3333(String message)  {
								System.out.println("消费者3 | "+ "topic_queue3: " + message);
				}

				@RabbitListener(queues = "object_queue")
				public void receiveMessage11111(Map<String, Object> message) {
								System.out.println("消费者 | " + "object_queue: " + message);
				}

				@RabbitListener(
													queuesToDeclare = @Queue(
																					name = "lazy_queue",
																					durable = "true",
																					arguments = @Argument(name = "x-queue-mode", value = "lazy")
													)
				)
				public void receiveMessage33333(String message)  {
								System.out.println("lazy_queue: " + message);
				}
}
