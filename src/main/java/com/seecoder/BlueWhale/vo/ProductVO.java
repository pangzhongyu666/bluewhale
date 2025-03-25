package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ProductVO {
				private Integer productId;

				private String name;

				private ProductTypeEnum type;

				private Integer price;

				private String  description;

				private Integer inventory;//库存

				private List<String> productImages;

				private Integer storeId;//所属商店

				private Double productRating;

				private  Integer productRatingCount;

				private Integer sales;



				public Product toPO(){
								Product  product = new Product();
								product.setProductId(this.productId);
								product.setName(this.name);
								product.setType(this.type);
								product.setPrice(this.price);
								product.setInventory(this.inventory);
								product.setProductImages(this.productImages);
								product.setStoreId(this.storeId);
								product.setDescription(this.description);
								product.setProductRating(this.productRating);
								product.setProductRatingCount(this.productRatingCount);
								product.setSales(this.sales);
								return product;
				}

}
