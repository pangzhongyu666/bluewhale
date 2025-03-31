package com.seecoder.BlueWhale.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisLock implements ILock{

				String name;
				private static final String keyPrefix = "lock:";
				private static final String IdPrefix = UUID.randomUUID().toString() + "-";
				private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
				static {
								UNLOCK_SCRIPT = new DefaultRedisScript<>();
								UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
								UNLOCK_SCRIPT.setResultType(Long.class);
				}



				private final RedisTemplate redisTemplate;

				public RedisLock(String name, RedisTemplate redisTemplate) {
								this.name = name;
								this.redisTemplate = redisTemplate;
				}


				@Override
				public boolean tryLock(long timeoutSec) {
								// 获取线程标识
								String threadId = IdPrefix + Thread.currentThread().getId() + "";
								Boolean res = redisTemplate.opsForValue().setIfAbsent(keyPrefix + name, threadId, timeoutSec, TimeUnit.SECONDS);
								return Boolean.TRUE.equals(res);
				}
//				@Override
//				public void unlock() {
//								// 获取线程标识
//								String threadId = IdPrefix + Thread.currentThread().getId() + "";
//								// 获取锁中的标识
//								String id = (String) redisTemplate.opsForValue().get(keyPrefix + name);
//
//								// 判断标识是否一致,防止误删
//								if (threadId.equals(id)) {
//												// 释放锁
//												redisTemplate.delete(keyPrefix + name);
//								}
//				}
				@Override
				public void unlock() {
								//基于lua脚本,让判断和删除原子性执行
								redisTemplate.execute(
												UNLOCK_SCRIPT,
												Collections.singletonList(keyPrefix + name),
												IdPrefix + Thread.currentThread().getId()
								);
				}
}
