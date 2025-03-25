package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.po.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {
				private Integer orderId;

				private String tradeName;

				private DeliveryEnum deliveryOption;

				private OrderStateEnum state;

				private Integer quantity;

				private Integer storeId;

				private Integer productId;

				private Integer userId;

				private Double paid;

				private Date createTime;

				private Date finishTime;



				public Order toPO() {
								Order order = new Order();
								order.setOrderId(this.orderId);
								order.setTradeName(this.tradeName);
								order.setDeliveryOption(this.deliveryOption);
								order.setState(this.state);
								order.setProductId(this.productId);
								order.setUserId(this.userId);
								order.setStoreId(this.storeId);
								order.setQuantity(this.quantity);
								order.setPaid(this.paid);
								order.setCreateTime(this.createTime);
								order.setFinishTime(this.finishTime);
								return order;
				}
}
