package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.util.RSAUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

}
