package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.vo.CartVO;

import java.util.List;

public interface CartService {
        Boolean create(CartVO cartVO);

        CartVO getCart(Integer userId);
        Integer addProduct(Integer userId, Integer productId, Integer count);
        Integer changeCount(Integer userId, Integer productId, Integer count);
        Integer removeProduct(Integer userId, Integer productId);

        Integer chooseProduct(Integer userId, Integer productId);

        Integer cancelChooseProduct(Integer userId, Integer productId);

        Boolean createOrders(Integer userId);

        Boolean checkPayResult(Integer userId);
        Boolean clear(Integer userId);
}
