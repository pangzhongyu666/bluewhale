package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.configure.AlipayTools;
import com.seecoder.BlueWhale.service.CartService;
import com.seecoder.BlueWhale.vo.CartVO;
import com.seecoder.BlueWhale.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    AlipayTools alipayTools;
    @GetMapping("/getCart/{userId}")
    public ResultVO<CartVO> getCart(@PathVariable(value = "userId")Integer userId){
        return ResultVO.buildSuccess(cartService.getCart(userId));
    }
    @PostMapping("/addProduct")
    public ResultVO<Integer> addProduct(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "productId") Integer productId,
            @RequestParam(value = "count") Integer count){
            return ResultVO.buildSuccess(cartService.addProduct(userId, productId, count));
    }
    @PostMapping("/changeCount")
    public ResultVO<Integer> changeCount(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "productId") Integer productId,
            @RequestParam(value = "count") Integer count
    ){
            return ResultVO.buildSuccess(cartService.changeCount(userId, productId, count));
    }

    @PostMapping("/removeProduct")
    public ResultVO<Integer> removeProduct(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "productId") Integer productId
    ){
        return ResultVO.buildSuccess(cartService.removeProduct(userId, productId));
    }
    @PostMapping("/createOrders/{userId}")
    public ResultVO<Boolean> createOrders(
            @PathVariable("userId") Integer userId
    ){
        return ResultVO.buildSuccess(cartService.createOrders(userId));
    }

    @PostMapping("/chooseProduct")
    public ResultVO<Integer> chooseProduct(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "productId") Integer productId
    ){
        return ResultVO.buildSuccess(cartService.chooseProduct(userId, productId));
    }
    @PostMapping("/cancelChooseProduct")
    public ResultVO<Integer> cancelChooseProduct(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "productId") Integer productId
    ){
        return ResultVO.buildSuccess(cartService.cancelChooseProduct(userId, productId));
    }
    @GetMapping("/checkPayResult/{userId}")
    public ResultVO<Boolean> checkPayResult(@PathVariable("userId")Integer userId){
        return ResultVO.buildSuccess(cartService.checkPayResult(userId));
    }
    @PostMapping("/clear")
    public ResultVO<Boolean> clear(@RequestParam(value = "userId")Integer userId){
        return ResultVO.buildSuccess(cartService.clear(userId));
    }
}
