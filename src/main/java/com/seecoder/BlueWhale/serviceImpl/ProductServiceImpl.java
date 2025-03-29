package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Product;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.service.ProductService;
import com.seecoder.BlueWhale.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {
				@Autowired
				ProductRepository productRepository;
				@Autowired
				private RedisTemplate redisTemplate;
				private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

				@Override
				public Boolean create(ProductVO productVO){
								Product newproduct = productVO.toPO();
								productRepository.save(newproduct);
								logger.info("创建商品" + productVO.getName() + ",id为" + newproduct.getProductId());
								return true;
				}


				@Override

				public ProductVO getInfo(Integer productId) {
								String key = "ProductInfo" + productId;
								// 先从redis中获取商品信息
								ProductVO productInRedis = (ProductVO) redisTemplate.opsForValue().get(key);

								if (productInRedis != null) {
												productInRedis.setProductImages(productInRedis.getProductImageForRedis());
												productInRedis.setProductImageForRedis(null);
												logger.info("从redis中获取商品信息");
												return productInRedis;
								}

								Product product = productRepository.findByProductId(productId);
								if (product == null) {
												throw BlueWhaleException.productNotExists();
								}


								// 将商品信息存入redis
								ProductVO productVO = product.toVO();
								productVO.setProductImages(null);
								redisTemplate.opsForValue().set(key, productVO);
								logger.info("从数据库中获取商品信息");
								return productVO;
				}


				@Override
				public Boolean updateInformation(ProductVO productVO) {
								Product product = productRepository.findByProductId(productVO.getProductId());
								if(product==null){
												throw BlueWhaleException.productNotExists();
								}
								//更新传入的信息中不为null的项
								Optional.ofNullable(productVO.getInventory()).ifPresent(product::setInventory);
								Optional.ofNullable(productVO.getPrice()).ifPresent(product::setPrice);
								Optional.ofNullable(productVO.getType()).ifPresent(product::setType);
								Optional.ofNullable(productVO.getName()).ifPresent(product::setName);
								Optional.ofNullable(productVO.getDescription()).ifPresent(product::setDescription);
								productRepository.save(product);
								logger.info("更新商品信息");

								//删除redis缓存
								String key = "ProductInfo" + product.getProductId();
								redisTemplate.delete(key);
								return true;
				}

				@Override
				public List<ProductVO> searchProducts(String name, ProductTypeEnum type, Integer minPrice, Integer maxPrice, Integer page, Integer size) {
								Pageable pageable = PageRequest.of(page, size);
								List<Product> products = productRepository.findByConditions(name, type, minPrice, maxPrice,pageable).getContent();
								logger.info("分页搜索商品");
								return products.stream().map(Product::toVO).collect(Collectors.toList());
				}
				@Override
				public int getPageNum(String name, ProductTypeEnum type, Integer minPrice, Integer maxPrice, Integer page, Integer size){
					Pageable pageable = PageRequest.of(page, size);
					return productRepository.findByConditions(name, type, minPrice, maxPrice,pageable).getTotalPages();
				}
}
