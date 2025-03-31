package com.seecoder.BlueWhale.RedisTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedissonTest {
				private RLock rlock;

				@Autowired
				private RedissonClient redissonClient;
				@Before
				public void setUp() {
								rlock = redissonClient.getLock("myLock");
				}

				@Test
				public void testLock() {
								boolean isLocked = rlock.tryLock();
								if(!isLocked){
												System.out.println("获取锁失败   1");
								}

								try {
												System.out.println("获取锁成功   1");
												// 执行业务逻辑
												testLock2();
								} finally {
												// 释放锁
												System.out.println("释放锁   1");
												rlock.unlock();
								}
				}

				public void testLock2() {
								boolean isLocked = rlock.tryLock();
								if(!isLocked){
												System.out.println("获取锁失败   2");
								}

								try {
												System.out.println("获取锁成功   2");
												// 执行业务逻辑
												System.out.println("执行业务逻辑   2");
								} finally {
												// 释放锁
												System.out.println("释放锁   2");
												rlock.unlock();
								}
				}

}
