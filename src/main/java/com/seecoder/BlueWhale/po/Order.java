package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.vo.OrderVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "`order`",
								indexes ={@Index(name = "idx_state", columnList = "state"),
																@Index(name = "idx_order_id", columnList = "order_id"),
																@Index(name = "idx_store_id", columnList = "store_id"),
																@Index(name = "idx_product_id", columnList = "product_id"),
																@Index(name = "idx_user_id", columnList = "user_id"),
}
)
public class  Order {
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Id
				@Column(name = "order_id")
				private Integer orderId;

				@Basic
				@Column(name = "trade_name")
				private String tradeName;

				@Basic
				@Column(name = "delivery_option")
				@Enumerated(EnumType.STRING)
				private DeliveryEnum deliveryOption;

				@Basic
				@Column(name = "state")
				@Enumerated(EnumType.STRING)
				private OrderStateEnum state;

				@Basic
				@Column(name = "quantity")
				private Integer quantity;

				@Basic
				@Column(name = "store_id")
				private Integer storeId;

				@Basic
				@Column(name = "product_id")
				private Integer productId;

				@Basic
				@Column(name = "user_id")
				private Integer userId;

				@Basic
				@Column(name = "create_time")
				private Date createTime;

				@Basic
				@Column(name = "finish_time")
				private Date finishTime;

				@Basic
				@Column(name = "paid")
				private Double paid;


				public OrderVO toVO() {
								OrderVO orderVO = new OrderVO();
								orderVO.setOrderId(this.orderId);
								orderVO.setTradeName(this.tradeName);
								orderVO.setDeliveryOption(this.deliveryOption);
								orderVO.setState(this.state);
								orderVO.setProductId(this.productId);
								orderVO.setUserId(this.userId);
								orderVO.setStoreId(this.storeId);
								orderVO.setQuantity(this.quantity);
								orderVO.setCreateTime(this.createTime);
								orderVO.setFinishTime(this.finishTime);
								orderVO.setPaid(this.paid);
								return orderVO;
				}
}
