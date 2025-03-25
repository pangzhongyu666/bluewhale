package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.vo.CartVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cart",
        indexes ={
            @Index(name = "idx_cart_id", columnList = "cart_id"),
            @Index(name = "idx_user_id", columnList = "user_id")
        }
)
public class Cart {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "cart_id")
    private Integer cartId;

    @Basic
    @Column(name = "user_id")
    private int userId;

    @Basic
    @Column(name = "times")
    private int times;

    @ElementCollection
    @CollectionTable(name = "cart_product_id_list", joinColumns = @JoinColumn(name = "cart_id"))
    private List<Integer> productIdList;

    @ElementCollection
    @CollectionTable(name = "cart_product_counts", joinColumns = @JoinColumn(name = "cart_id"))
    private List<Integer> productCountList;

    @ElementCollection
    @CollectionTable(name = "cart_choose_list", joinColumns = @JoinColumn(name = "cart_id"))
    private List<Integer> chooseList;
    @Basic
    @Column(name = "total_price")
    private int totalPrice;

    @ElementCollection
    @CollectionTable(name = "cart_order_id_list", joinColumns = @JoinColumn(name = "cart_id"))
    private List<Integer> orderIdList;


    public CartVO toVO() {
            CartVO cartVO = new CartVO();
            cartVO.setCartId(this.cartId);
            cartVO.setUserId(this.userId);
            cartVO.setTimes(this.times);
            cartVO.setProductIdList(this.productIdList);
            cartVO.setProductCountList(this.productCountList);
            cartVO.setOrderIdList(this.orderIdList);
            cartVO.setChooseList(this.chooseList);
            cartVO.setTotalPrice(this.totalPrice);
            return cartVO;
    }
}
