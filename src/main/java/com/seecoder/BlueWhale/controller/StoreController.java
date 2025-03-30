package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
				@Autowired
				StoreService storeService;

				@PostMapping("/create")
				public ResultVO<Boolean> create(@RequestBody StoreVO storeVO){
								return ResultVO.buildSuccess(storeService.create(storeVO));
				}

				@PutMapping("/update")
				public ResultVO<Boolean> update(@RequestBody StoreVO storeVO){
								return ResultVO.buildSuccess(storeService.update(storeVO));
				}
				@GetMapping("/getInfo/{storeId}")
				public ResultVO<StoreVO> getInfo(@PathVariable(value="storeId")Integer storeId){
								return ResultVO.buildSuccess(storeService.getInfo(storeId));
				}

				@GetMapping("/getOneStoreProducts/{storeId}")
				public ResultVO<List<ProductVO>> getOneStoreProducts(@PathVariable(value="storeId")Integer storeId){
								return ResultVO.buildSuccess(storeService.getOneStoreProducts(storeId));
				}


				@GetMapping("/getAllStores")
				public ResultVO<List<StoreVO>> getAllStores(){
								return ResultVO.buildSuccess(storeService.getAllStores());
				}
}
