package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.CartRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.CartService;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.util.RSAUtil;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.vo.CartVO;
import com.seecoder.BlueWhale.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 注册登录功能实现
*/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CartService cartService;
    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public Boolean register(UserVO userVO) {
        User user = userRepository.findByPhone(userVO.getPhone());
        if (user != null) {
            throw BlueWhaleException.phoneAlreadyExists();
        }
        User newUser = userVO.toPO();
        newUser.setCreateTime(new Date());

        int userId = userRepository.save(newUser).getId();
        if(newUser.getRole() == RoleEnum.CUSTOMER) {
            CartVO cartVO = new CartVO();
            cartVO.setUserId(userId);
            cartService.create(cartVO);
        }
        logger.info("新用户" + newUser.getId() + "注册");
        return true;
    }

    @Override
    public String login(String phone, String password) {
        //System.out.println("加密密码:" + password);
        //String newPassword =  RSAUtil.decrypt(password);
        //System.out.println("解密密码:" + newPassword);
        //User user = userRepository.findByPhoneAndPassword(phone, newPassword);
        User user = userRepository.findByPhoneAndPassword(phone, password);
        if (user == null) {
            throw BlueWhaleException.phoneOrPasswordError();
        }
        logger.info("用户" + user.getId() + "登录");
        return tokenUtil.getToken(user);
    }


    @Override
    public UserVO getInformation() {
        User user=securityUtil.getCurrentUser();
        return user.toVO();
    }

    @Override
    public Boolean updateInformation(UserVO userVO) {
        User user=securityUtil.getCurrentUser();
        if (userVO.getPassword()!=null){
            user.setPassword(userVO.getPassword());
        }
        if (userVO.getName()!=null){
            user.setName(userVO.getName());
        }
        if (userVO.getAddress()!=null){
            user.setAddress(userVO.getAddress());
        }
        userRepository.save(user);
        logger.info("用户" + user.getId() + "信息更新");
        return true;
    }

    @Override
    public UserVO getUserInformation(Integer userId) {
        return userRepository.getOne(userId).toVO();        
    }



}
