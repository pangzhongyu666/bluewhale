package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.StoreService;
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

import java.util.*;
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

				@Override
				public Boolean create(StoreVO storeVO){
								Store store = storeRepository.findByName(storeVO.getName());
								if(store != null){
												throw BlueWhaleException.storeNameAlreadyExists();
								}
								Store newstore = storeVO.toPO();
								storeRepository.save(newstore);
								logger.info("创建商店" + newstore.getStoreId());
								return true;

				}
				@Override
				public List<StoreVO> getAllStores() {
								return storeRepository.findAll().stream().map(Store::toVO).collect(Collectors.toList());
				}
				@Override
				public StoreVO getInfo(Integer storeId) {
								String key = "StoreInfo" + storeId;
								// 先从redis中获取商店信息
								StoreVO storeInRedis = (StoreVO) redisTemplate.opsForValue().get(key);
								// 如果redis中没有，再从数据库中获取
								if(storeInRedis != null){
												return storeInRedis;
								}

								// 从数据库中获取
								Store store = storeRepository.findByStoreId(storeId);
								if(store == null){
												throw BlueWhaleException.storeNotExists();
								}
								// 存入redis
								redisTemplate.opsForValue().set(key, store.toVO());
								return store.toVO();
				}
				@Override
				public List<ProductVO> getOneStoreProducts(Integer storeId) {
								Store store1 = storeRepository.findByStoreId(storeId);
								if(store1 == null){
												throw BlueWhaleException.storeNotExists();
								}
								return productRepository.findByStoreId(storeId).stream().map(Product::toVO).collect(Collectors.toList());
				}

}
