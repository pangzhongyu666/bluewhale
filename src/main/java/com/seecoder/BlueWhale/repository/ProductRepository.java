package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.enums.ProductTypeEnum;
import com.seecoder.BlueWhale.po.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {


				List<Product> findByStoreId(int storeId);

				Product findByProductId(Integer productId);
				@Modifying
				@Query("UPDATE Product p SET p.inventory = p.inventory - :quantity WHERE p.productId = :productId AND p.inventory >= :quantity")
				//返回值是int，代表更新的行数
				int deductInventory(@Param("productId") Integer productId, @Param("quantity") int quantity);

				@Query("SELECT p FROM Product p WHERE (:name IS NULL OR p.name LIKE %:name%) " +
												"AND (:type IS NULL OR p.type = :type) " +
												"AND (:minPrice IS NULL OR p.price >= :minPrice) " +
												"AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
				Page<Product> findByConditions(@Param("name") String name,
																																			@Param("type") ProductTypeEnum type,
																																			@Param("minPrice") Integer minPrice,
																																			@Param("maxPrice") Integer maxPrice,
																																			Pageable pageable);


}
