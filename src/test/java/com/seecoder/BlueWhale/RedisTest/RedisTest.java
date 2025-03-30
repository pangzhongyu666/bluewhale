package com.seecoder.BlueWhale.RedisTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.po.Store;

import com.seecoder.BlueWhale.util.RedisData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

				@Autowired
				private RedisTemplate<String,Object> redisTemplate;

				@Autowired
				private StringRedisTemplate stringRedisTemplate;
				@Test
				public void testRedisString(){
								// 存储字符串
								redisTemplate.opsForValue().set("name", "John Doe");
								// 获取字符串
								String name = (String) redisTemplate.opsForValue().get("name");
								System.out.println("Name: " + name);
				}
				@Test
				public void testRedisSaveUser(){
								User user = new User();
								user.setId(99999);
								user.setName("庞忠宇");
								user.setPhone("18898663386");
								redisTemplate.opsForValue().set("user:" + user.getId(), user);
				}
				@Test
				public void testRedisHash(){
								redisTemplate.opsForHash().put("cartId", "productId", "productNum");
								redisTemplate.opsForHash().put("cartId", "productId2", "productNum2");
								redisTemplate.opsForHash().put("cartId", "productId3", "productNum3");
								redisTemplate.opsForHash().put("cartId", "productId4", "productNum4");
								// 获取哈希表中的所有键值对
								System.out.println(redisTemplate.opsForHash().entries("cartId"));
								// 获取哈希表中指定字段的值
								System.out.println(redisTemplate.opsForHash().get("cartId", "productId"));
				}

				private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
				@Test
				public void testRedisString2() throws JsonProcessingException {
								User user = new User();
								user.setId(999999);
								user.setName("庞忠宇");
								user.setPhone("18898663386");

								// 将对象转换为 JSON 字符串
								String userJson = OBJECT_MAPPER.writeValueAsString(user);
								stringRedisTemplate.opsForValue().set("user:" + user.getId(), userJson);
				}
}
