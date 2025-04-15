package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.util.RSAUtil;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true) // 开启AOP功能
public class BlueWhaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueWhaleApplication.class, args);
		RSAUtil.initKey();
	}

	@Bean
				public MessageConverter messageConverter() {
					//定义消息转换器，将消息对象序列化为JSON格式
					Jackson2JsonMessageConverter jjmc = new Jackson2JsonMessageConverter();
					//配置自动创建消息id，用于标识消息的唯一性
					//jjmc.setCreateMessageIds(true);
					return jjmc;
				}

}
