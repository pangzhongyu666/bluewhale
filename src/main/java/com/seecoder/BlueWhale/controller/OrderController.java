package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.vo.OrderVO;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
				@Autowired
				OrderService orderService;

				@PostMapping("/create")
				public ResultVO<OrderVO> create(@RequestBody OrderVO orderVO){
								return ResultVO.buildSuccess(orderService.create(orderVO));
				}

				@GetMapping("/getUserOrders/{userId}")
				public ResultVO<List<OrderVO>> getUserOrders(@PathVariable(value="userId")Integer userId){
								return ResultVO.buildSuccess(orderService.getUserOrders(userId));
				}

				@GetMapping("/getStoreOrders/{storeId}")
				public ResultVO<List<OrderVO>> getStoreOrders(@PathVariable(value="storeId")Integer storeId){
								return ResultVO.buildSuccess(orderService.getStoreOrders(storeId));
				}

				@GetMapping("/getProductOrders/{productId}")
				public ResultVO<List<OrderVO>> getProductOrders(@PathVariable(value="productId")Integer productId){
								return ResultVO.buildSuccess(orderService.getProductOrders(productId));
				}

				@GetMapping("/getAllOrders")
				public ResultVO<List<OrderVO>> getAllOrders(){
								return ResultVO.buildSuccess(orderService.getAllOrders());
				}

				@PostMapping("/updateInfo")
				public ResultVO<Boolean> updateInformation(@RequestBody OrderVO orderVO){
								return ResultVO.buildSuccess(orderService.updateInformation(orderVO));
				}

				@GetMapping("/checkPaySuccess/{orderId}")
				public ResultVO<Boolean> checkPaySuccess(@PathVariable(value="orderId")Integer orderId){
								return ResultVO.buildSuccess(orderService.checkPaySuccess(orderId));
				}

}
