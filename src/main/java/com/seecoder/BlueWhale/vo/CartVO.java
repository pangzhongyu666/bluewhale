package com.seecoder.BlueWhale.vo;


import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.po.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartVO {
        //购物车Id
        private int cartId;
        //购物车所属用户Id
        private int userId;
        //购物车下单次数
        private int times;
        //购物车中商品列表
        private List<Integer> productIdList;
        //购物车中商品对应数量
        private List<Integer> productCountList;
        //购物车中商品总价
        private int totalPrice;
        //被选中的商品列表
        private List<Integer> chooseList;
        //从购物车中同时创建的订单列表
        private List<Long> orderIdList;
        public Cart toPO(){
                Cart cart = new Cart();
                cart.setCartId(this.cartId);
                cart.setUserId(this.userId);
                cart.setTimes(this.times);
                cart.setProductIdList(this.productIdList);
                cart.setProductCountList(this.productCountList);
                cart.setOrderIdList(this.orderIdList);
                cart.setChooseList(this.chooseList);
                cart.setTotalPrice(this.totalPrice);
                return cart;
        }
}
