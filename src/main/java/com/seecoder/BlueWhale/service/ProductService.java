package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.po.Review;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.ReviewVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import com.seecoder.BlueWhale.vo.UserVO;

import java.util.List;

public interface ProductService {

				Boolean create (ProductVO productVO);
				ProductVO getInfo(Integer productId);
				Boolean updateInformation(ProductVO productVO);
				List<ProductVO> searchProducts(String name, ProductTypeEnum type, Integer minPrice, Integer maxPrice, Integer page, Integer size);
				int getPageNum(String name, ProductTypeEnum type, Integer minPrice, Integer maxPrice, Integer page, Integer size);
}