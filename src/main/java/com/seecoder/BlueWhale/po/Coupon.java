package com.seecoder.BlueWhale.po;


import com.seecoder.BlueWhale.enums.CouponStateEnum;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.seecoder.BlueWhale.vo.CouponVO;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coupon",indexes = {
        @Index(name = "idx_coupon_id", columnList = "coupon_id"),
        @Index(name = "idx_coupon_group_id", columnList = "coupon_group_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_c_u_id", columnList = "store_id,user_id"),

})
public class Coupon {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "coupon_id")
    private Integer couponId;

    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "store_id")
    private Integer storeId; //0表示全局

    @Basic
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private CouponStateEnum state;

    @Basic
    @Column(name = "coupon_group_id")
    private Integer couponGroupId;


    public CouponVO toVO(){
        CouponVO couponVO = new CouponVO();
        couponVO.setCouponId(this.couponId);
        couponVO.setState(this.state);
        couponVO.setUserId(this.userId);
        couponVO.setStoreId(this.storeId);
        couponVO.setCouponGroupId(this.couponGroupId);
        return couponVO;
    }
}
