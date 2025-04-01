package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.aspect.UserApiMonitorAspect;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.util.RSAUtil;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserApiMonitorAspect userApiMonitorAspect;

    // 获取统计数据
    @GetMapping("/getStats")
    public ResultVO<Map<String, Map<String, Object>>> getStats() {
        try {
            Map<String, Map<String, Object>> stats = userApiMonitorAspect.getApiStats();
            return ResultVO.buildSuccess(stats);
        } catch (Exception e) {
            return ResultVO.buildFailure("获取接口统计信息失败");
        }
    }
    @Autowired
    UserService userService;

    @GetMapping("/getPublicKeyStr")
    public ResultVO<String> getPublicKeyStr(){
        System.out.println("前端获取公钥" + RSAUtil.getPublicKeyStr());
        return ResultVO.buildSuccess(RSAUtil.getPublicKeyStr());
    }

    @PostMapping("/register")
    public ResultVO<Boolean> register(@RequestBody UserVO userVO){
        return ResultVO.buildSuccess(userService.register(userVO));
    }

    @PostMapping("/login")
    public ResultVO<String> login(@RequestParam("phone") String phone, @RequestParam("password") String password){
        return ResultVO.buildSuccess(userService.login(phone, password));
    }

    @GetMapping("/getInfo/{userId}")
    public ResultVO<UserVO> getUserInformation(@PathVariable(value="userId")Integer userId){
        return ResultVO.buildSuccess(userService.getUserInformation(userId));
    }
    @GetMapping
    public ResultVO<UserVO> getInformation(){
        return ResultVO.buildSuccess(userService.getInformation());
    }

    @PostMapping
    public ResultVO<Boolean> updateInformation(@RequestBody UserVO userVO){
        return ResultVO.buildSuccess(userService.updateInformation(userVO));
    }

    @GetMapping("/getPrivileges")
    public ResultVO<List<String>> getCurrUserPrivileges(){
        return ResultVO.buildSuccess(userService.getCurrUserPrivileges());
    }

    @PostMapping("/sign")
    public ResultVO<Boolean> sign(){
        return ResultVO.buildSuccess(userService.sign());
    }
}
