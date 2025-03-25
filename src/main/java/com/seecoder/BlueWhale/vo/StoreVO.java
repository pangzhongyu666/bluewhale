package com.seecoder.BlueWhale.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.seecoder.BlueWhale.po.Store;



@Getter
@Setter
@NoArgsConstructor
public class StoreVO {

				private Integer storeId;

				private String name;

				private String logoLink;

				private Double storeRating;

				private  Integer storeRatingCount;

				private Integer sales;

				public Store toPO(){
								Store  store = new Store();
								store.setStoreId(this.storeId);
								store.setName(this.name);
								store.setLogoLink(this.logoLink);
								store.setStoreRating(this.storeRating);
								store.setStoreRatingCount(this.storeRatingCount);
								store.setSales(this.sales);
								return store;
				}

}
