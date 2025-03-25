package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

				List<Review> findByProductId(int productId);
				List<Review> findByParentId(int parentId);

}
