package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
        Cart findByCartId(Integer cartId);
        Cart findByUserId(Integer userId);
}
