package com.seecoder.BlueWhale.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
				private static final long BEGIN_TIMESTAMP = 1640995200L;
				private final static long COUNT_BITS = 32;

				private RedisTemplate redisTemplate;

				public RedisIdWorker(RedisTemplate redisTemplate) {
								this.redisTemplate = redisTemplate;
				}

				public long nextId(String keyPrefix){
								//1位符号位，31位时间戳，32位序列号

								//1.生成时间戳
								LocalDateTime now = LocalDateTime.now();
								long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
								long timestamp = nowSecond - BEGIN_TIMESTAMP;//序列号位数

								//2.生成序列号
								//获取当前日期
								String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
								long count = redisTemplate.opsForValue().increment("incre:" + keyPrefix + ":" + date);

								//3.拼接并返回
								return timestamp << COUNT_BITS | count;
				}

				public static void main(String[] args) {
								LocalDateTime time = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
								long second = time.toEpochSecond(ZoneOffset.UTC	);
								System.out.println(second);

				}
}
