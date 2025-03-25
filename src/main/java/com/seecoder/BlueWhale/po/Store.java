package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.vo.StoreVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "store",
								indexes ={@Index(name = "idx_store_id", columnList = "store_id"),
																@Index(name = "idx_name", columnList = "name"),
								}
)
public class Store {
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				@Id
				@Column(name = "store_id")
				private Integer storeId;

				@Basic
				@Column(name = "name")
				private String name;

				@Basic
				@Column(name = "logo_link")
				private String logoLink;

				@Basic
				@Column(name = "store_rating")
				private Double storeRating;

				@Basic
				@Column(name = "store_rating_count")
				private  Integer storeRatingCount;

				@Basic
				@Column(name = "sales")
				private Integer sales;



				public StoreVO toVO(){
								StoreVO  storeVO = new StoreVO();
								storeVO.setStoreId(this.storeId);
								storeVO.setName(this.name);
								storeVO.setLogoLink(this.logoLink);
								storeVO.setStoreRating(this.storeRating);
								storeVO.setStoreRatingCount(this.storeRatingCount);
								storeVO.setSales(this.sales);
								return storeVO;
				}
}
