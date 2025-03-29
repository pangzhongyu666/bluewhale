package com.seecoder.BlueWhale.configure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.seecoder.BlueWhale.serviceImpl.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.lang.reflect.Method;
import java.time.Duration;
@Configuration
public class RedisConfig {

				@Bean
				public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
								//创建RedisTemplate对象
								RedisTemplate<String, Object> template = new RedisTemplate<>();

								//配置连接工厂
								template.setConnectionFactory(factory);

								//创建JSON序列化工具
								GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
								//设置key的序列化
								template.setKeySerializer(RedisSerializer.string());
								template.setHashKeySerializer(RedisSerializer.string());
								//设置value的序列化
								template.setValueSerializer(jackson2JsonRedisSerializer);
								template.setHashValueSerializer(jackson2JsonRedisSerializer);
								//返回
								return template;
				}
}