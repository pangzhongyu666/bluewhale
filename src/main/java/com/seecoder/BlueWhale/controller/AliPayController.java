package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.configure.AlipayTools;
import com.seecoder.BlueWhale.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/ali")
public class AliPayController {
    @Autowired
    AlipayTools alipayTools;


    @PostMapping("/pay")
    public ResultVO<String> pay(@RequestParam("tradeName")String tradeName, @RequestParam("name")String name, @RequestParam("price")Double price, @RequestParam("path")String path,@RequestParam("couponId")Integer couponId) {
        return ResultVO.buildSuccess(alipayTools.pay(tradeName, name, price,path,couponId));
    }

    @PostMapping("/refund")
    public ResultVO<Boolean> refund(@RequestParam("orderId")Integer orderId) {
        return ResultVO.buildSuccess(alipayTools.refund(orderId));
    }
    @PostMapping("/notify/{couponId}")
    public void notify(HttpServletRequest httpServletRequest,@PathVariable("couponId")Integer couponId){
        alipayTools.notify(httpServletRequest,couponId);
    }
    @PostMapping("/payCart")
    public ResultVO<String> payCart(@RequestParam("tradeName")String tradeName, @RequestParam("name")String name, @RequestParam("price")Double price, @RequestParam("path")String path,@RequestParam("cartId")Integer cartId) {
        return ResultVO.buildSuccess(alipayTools.payCart(tradeName, name, price,path,cartId));
    }
    @PostMapping("/notifyCart/{cartId}")
    public void notifyCart(HttpServletRequest httpServletRequest, @PathVariable("cartId")Integer cartId){
        alipayTools.notifyCart(httpServletRequest,cartId);
    }

}
