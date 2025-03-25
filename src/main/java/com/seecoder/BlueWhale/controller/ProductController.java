package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.service.ProductService;
import com.seecoder.BlueWhale.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

				@Autowired
				ProductService productService;
				@PostMapping("/create")
				public ResultVO<Boolean> create(@RequestBody ProductVO productVO){
								return ResultVO.buildSuccess(productService.create(productVO));
				}
				@GetMapping("/getInfo/{productId}")
				public ResultVO<ProductVO> getInfo(@PathVariable(value="productId")Integer productId){
								return ResultVO.buildSuccess(productService.getInfo(productId));
				}
				@PostMapping("/updateInfo")
				public ResultVO<Boolean> updateInformation(@RequestBody ProductVO productVO){
								return ResultVO.buildSuccess(productService.updateInformation(productVO));
				}

				@PostMapping("/search")
				public ResultVO<List<ProductVO>> searchProducts(
						@RequestParam(required = false, value="name")     String name,
						@RequestParam(required = false, value="type")     ProductTypeEnum type,
						@RequestParam(required = false, value="minPrice") Integer minPrice,
						@RequestParam(required = false, value="maxPrice") Integer maxPrice,
						@RequestParam(value="page") Integer page,
						@RequestParam(value="size") Integer size) {
					return ResultVO.buildSuccess(productService.searchProducts(name,type,minPrice,maxPrice,page,size));
				}

				@PostMapping("/getPageNum")
				public ResultVO<Integer> getPageNum(
						@RequestParam(required = false, value="name")     String name,
						@RequestParam(required = false, value="type")     ProductTypeEnum type,
						@RequestParam(required = false, value="minPrice") Integer minPrice,
						@RequestParam(required = false, value="maxPrice") Integer maxPrice,
						@RequestParam(value="page") Integer page,
						@RequestParam(value="size") Integer size) {
					return ResultVO.buildSuccess(productService.getPageNum(name,type,minPrice,maxPrice,page,size));
				}


}
