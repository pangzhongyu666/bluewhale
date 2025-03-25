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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
				private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    /**采用RedisCacheManager作为缓存管理器
     * 为了处理高可用Redis，可以使用RedisSentinelConfiguration来支持Redis Sentinel
     */
				@Bean
				public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
								// 分别创建String和JSON格式序列化对象，对缓存数据key和value进行转换
								RedisSerializer<String> strSerializer = new StringRedisSerializer();
								Jackson2JsonRedisSerializer jacksonSerial = new Jackson2JsonRedisSerializer(Object.class);
								// 解决查询缓存转换异常的问题
								ObjectMapper om = new ObjectMapper();
								om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
								om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY); // 上面注释过时代码的替代方法
								jacksonSerial.setObjectMapper(om);
								// 定制缓存数据序列化方式及时效
								RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
																.entryTtl(Duration.ofDays(1)) // 设置缓存数据的时效（设置为了1天）
																.serializeKeysWith(RedisSerializationContext.SerializationPair
																								.fromSerializer(strSerializer)) // 对当前对象的key使用strSerializer这个序列化对象，进行转换
																.serializeValuesWith(RedisSerializationContext.SerializationPair
																								.fromSerializer(jacksonSerial)) // 对value使用jacksonSerial这个序列化对象，进行转换
																.disableCachingNullValues();
								RedisCacheManager cacheManager = RedisCacheManager
																.builder(redisConnectionFactory).cacheDefaults(config).build();
								return cacheManager;
				}

    /**
												* 自定义生成key的规则
     */

				@Override
				public KeyGenerator keyGenerator() {
								return new KeyGenerator() {
												@Override
												public Object generate(Object o, Method method, Object...objects) {
																// 格式化缓存key字符串
																StringBuilder stringBuilder = new StringBuilder();
																// 追加类名
																stringBuilder.append(o.getClass().getName());
																// 追加方法名
																stringBuilder.append(".").append(method.getName());
																// 遍历参数并且追加
																for (Object obj :objects) {
																				stringBuilder.append(obj.toString()).append(" ");
																}
																logger.info("调用Redis缓存Key: " + stringBuilder.toString());
																return stringBuilder.toString();
												}
								};
				}
}