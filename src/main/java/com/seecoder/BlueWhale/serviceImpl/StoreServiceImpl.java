package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.util.RedisData;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {
				@Autowired
				ProductRepository productRepository;
				@Autowired
				StoreRepository storeRepository;
				private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

				@Autowired
				private RedisTemplate redisTemplate;

				private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

				@Override
				public Boolean create(StoreVO storeVO){
								Store store = storeRepository.findByName(storeVO.getName());
								if(store != null){
												throw BlueWhaleException.storeNameAlreadyExists();
								}
								Store newstore = storeVO.toPO();
								storeRepository.save(newstore);
								redisTemplate.opsForValue().set("StoreInfo" + newstore.getStoreId(), newstore.toVO());
								redisTemplate.opsForHash().put("AllStores", newstore.getStoreId() + "", newstore.toVO());
								logger.info("创建商店" + newstore.getStoreId());

								return true;

				}
				//获取商店排行榜
				@Override
				public List<StoreVO> getStoreRank() {
								//从redis中获取
								List<StoreVO> storesInRedis = new ArrayList<>();
								Set<String> top10 = redisTemplate.opsForZSet().range("StoreRank", 0, 9);

								if (top10 != null) {
												for(String storeId : top10){
																StoreVO storeVO = getInfo(Integer.valueOf(storeId));
																storesInRedis.add(storeVO);
												}
								}
								return storesInRedis;
				}
				@Override
				public List<StoreVO> getAllStores() {
								// 从redis中获取所有商店信息
								List<StoreVO> storesInRedis = new ArrayList<>();
								for(Object o : redisTemplate.opsForHash().values("AllStores")){
												StoreVO storeVO = (StoreVO) o;
												storesInRedis.add(storeVO);
								}
								if(!storesInRedis.isEmpty()){
												return storesInRedis;
								}
								// 从数据库中获取
								List<Store> stores = storeRepository.findAll();
								// 将商店信息存入redis
								for(Store store : stores){
												redisTemplate.opsForHash().put("AllStores", store.getStoreId() + "", store.toVO());
								}
								return stores.stream().map(Store::toVO).collect(Collectors.toList());
				}
				@Override
				public StoreVO getInfo(Integer storeId) {
								//缓存穿透
								//StoreVO storeVO = queryWithPassThrough(storeId);
								//缓存击穿
								StoreVO storeVO = queryWithMutex(storeId);

								//逻辑过期
								//StoreVO storeVO = queryWithLogicalExpire(storeId);

								if(storeVO == null){
												throw BlueWhaleException.storeNotExists();
								}
								return storeVO;
				}
				private StoreVO queryWithPassThrough(Integer storeId){
								String key = "StoreInfo" + storeId;
								//如果是缓存的空值，则抛出异常
								if(redisTemplate.opsForValue().get(key) == ""){
												throw BlueWhaleException.storeNotExists();
								}

								// 先从redis中获取商店信息
								StoreVO storeInRedis = (StoreVO) redisTemplate.opsForValue().get(key);
								// 如果redis中没有，再从数据库中获取
								if(storeInRedis != null){
												return storeInRedis;
								}


								// 从数据库中获取
								Store store = storeRepository.findByStoreId(storeId);
								if(store == null){
												//将空值写入redis，防止缓存穿透
												redisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
												throw BlueWhaleException.storeNotExists();
								}
								// 存入redis
								redisTemplate.opsForValue().set(key, store.toVO(), 5L, TimeUnit.MINUTES);

								return store.toVO();
				}

				private StoreVO queryWithMutex(Integer storeId) {
								String key = "StoreInfo" + storeId;

								// 先从redis中获取商店信息
								StoreVO storeInRedis = (StoreVO) redisTemplate.opsForValue().get(key);
								// 如果redis中没有，再从数据库中获取
								if (storeInRedis != null) {
												return storeInRedis;
								}
								//获取锁
								String lockKey = null;
								Store store;
								try {
												lockKey = "StoreLock" + storeId;
												if (!tryLock(lockKey)) {
																//获取锁失败，等待一段时间后重试
																Thread.sleep(100);
																return getInfo(storeId);
												}
												//获取锁成功

												// 从数据库中获取
												store = storeRepository.findByStoreId(storeId);
												if (store == null) {
																throw BlueWhaleException.storeNotExists();
												}
												// 存入redis
												redisTemplate.opsForValue().set(key, store.toVO(), 5L, TimeUnit.MINUTES);
								} catch (InterruptedException e) {
												throw new RuntimeException(e);
								} finally {
												//释放锁
												unlock(lockKey);
								}
								return store.toVO();
				}

				private StoreVO queryWithLogicalExpire(Integer storeId){
								String key = "StoreInfo" + storeId;

								Object objectInRedis = redisTemplate.opsForValue().get(key);
								// 这里未命中不会查询数据库，因为默认进行了数据预热，如果没命中说明不是热点数据直接返回即可
								if(objectInRedis == null){
												return null;
								}
								//命中，判断是否过期
								RedisData redisData = (RedisData) objectInRedis;
								LocalDateTime expireTime = redisData.getExpireTime();

								//未过期，直接返回
								if(expireTime.isAfter(LocalDateTime.now())){
												logger.info("未过期，直接返回");
												return (StoreVO) redisData.getData();
								}
								//已过期，获取锁
								String lockKey = "StoreLock" + storeId;
								//获取锁成功
								if(tryLock(lockKey)){
												//双重检查,因为可能有其他线程已经重建了缓存
												objectInRedis = redisTemplate.opsForValue().get(key);
												redisData = (RedisData) objectInRedis;
												expireTime = redisData.getExpireTime();
												//未过期，直接返回
												if(expireTime.isAfter(LocalDateTime.now())){
																return (StoreVO) redisData.getData();
												}

												//已过期，重建缓存
												//开启独立线程，实现缓存重建
												CACHE_REBUILD_EXECUTOR.submit(() -> {
																try {
																				saveCacheShopWithLogicalExpire(storeId, 20L);
																} catch (Exception e) {
																				throw new RuntimeException(e);
																} finally {
																				//释放锁
																				unlock(lockKey);
																}
												});
								}
								//获取锁失败，直接返回旧数据
								//不保障一致性
								return (StoreVO) redisData.getData();
				}

				@Override
				@Transactional
				public Boolean update(StoreVO storeVO) {
								Store store = storeRepository.findByStoreId(storeVO.getStoreId());
								if(store == null){
												throw BlueWhaleException.storeNotExists();
								}
								Optional.ofNullable(storeVO.getName()).ifPresent(store::setName);
								Optional.ofNullable(storeVO.getLogoLink()).ifPresent(store::setLogoLink);
								storeRepository.save(store);

								//删除redis中的商店信息
								redisTemplate.delete("StoreInfo" + store.getStoreId());
								redisTemplate.delete("AllStores");
								return true;
				}
				@Override
				public List<ProductVO> getOneStoreProducts(Integer storeId) {
								Store store1 = storeRepository.findByStoreId(storeId);
								if(store1 == null){
												throw BlueWhaleException.storeNotExists();
								}
								return productRepository.findByStoreId(storeId).stream().map(Product::toVO).collect(Collectors.toList());
				}


				private boolean tryLock(String key){
								Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
								return Objects.nonNull(flag) && flag;
				}

				private void unlock(String key){
								redisTemplate.delete(key);
				}

				public void saveCacheShopWithLogicalExpire(Integer storeId, Long time){
								logger.info("重建缓存" + storeId);
								//1.查询店铺数据
								Store store = storeRepository.findByStoreId(storeId);
								//2.封装逻辑过期时间
								RedisData redisData = new RedisData();
								redisData.setData(store.toVO());
								redisData.setExpireTime(LocalDateTime.now().plusSeconds(time));

								//3.写入redis
								redisTemplate.opsForValue().set("StoreInfo" + storeId, redisData);
				}
}
