package com.seecoder.BlueWhale.RedisTest;

import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.serviceImpl.StoreServiceImpl;
import com.seecoder.BlueWhale.util.RedisIdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDataTest {

				@Autowired
				private StoreServiceImpl storeServiceImpl;

				@Autowired
				RedisIdWorker redisIDWorker;

				@Test
				public void testRedisData(){
								storeServiceImpl.saveCacheShopWithLogicalExpire(1, 10L);
				}

				private final ExecutorService es = Executors.newFixedThreadPool(500);

				@Test
				public void testRedisIdWorker(){
								Runnable task = () -> {
												for(int i = 0; i < 300; i++){
																System.out.println(redisIDWorker.nextId("order"));
												}
								};
								for(int i = 0; i < 300; i++){
												es.submit(task);
								}

								// 新增线程池关闭和等待 ↓
								es.shutdown();
								while (!es.isTerminated()) {
												try {
																es.awaitTermination(1, TimeUnit.MINUTES);
												} catch (InterruptedException e) {
																e.printStackTrace();
												}
								}
								// 新增线程池关闭和等待 ↑
				}
}
