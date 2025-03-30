package com.seecoder.BlueWhale.util;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheClient {
				private final RedisTemplate redisTemplate;

				public CacheClient(RedisTemplate redisTemplate){
								this.redisTemplate = redisTemplate;
				}

				public void set(String key, Object value, Long time, TimeUnit unit){
								redisTemplate.opsForValue().set(key, value, time, unit);
				}

}
