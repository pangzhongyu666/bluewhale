package com.seecoder.BlueWhale.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.vo.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product", indexes = {
								@Index(name = "idx_product_id", columnList = "product_id"),
								@Index(name = "idx_store_id", columnList = "store_id"),
								@Index(name = "idx_name_type_price", columnList = "name, type, price")
})
public class Product {
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Id
				@Column(name = "product_id")
				private Integer productId;

				@Basic
				@Column(name = "name")
				private String name;

				@Basic
				@Column(name = "type")
				@Enumerated(EnumType.STRING)
				private ProductTypeEnum type;

				@Basic
				@Column(name = "price")
				private Integer price;

				@Basic
				@Column(name = "description")
				private String  description;

				@Basic
				@Column(name = "inventory")//库存
				private Integer inventory;

				@Basic
				@Column(name = "product_rating")
				private Double productRating;

				@Basic
				@Column(name = "product_rating_count")
				private  Integer productRatingCount;

				@ElementCollection
				@JsonIgnore
				@CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
				private List<String> productImages;

				@Transient
				private List<String> productImageForRedis;

				@Basic
				@Column(name = "store_id")
				private Integer storeId;

				@Basic
				@Column(name = "sales")
				private Integer sales;

				public ProductVO toVO(){
								ProductVO  productVO = new ProductVO();
								productVO.setProductId(this.productId);
								productVO.setName(this.name);
								productVO.setPrice(this.price);
								productVO.setType(this.type);
								productVO.setInventory(this.inventory);
								productVO.setProductImages(this.productImages);
								productVO.setProductImageForRedis(new ArrayList<>(this.productImages)); // 复制列表，避免 Hibernate 代理
								productVO.setStoreId(this.storeId);
								productVO.setDescription(this.description);
								productVO.setProductRating(this.productRating);
								productVO.setProductRatingCount(this.productRatingCount);
								productVO.setSales(this.sales);
								return productVO;
				}

}
