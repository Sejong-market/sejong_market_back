package com.example.market.repository;

import com.example.market.entity.Product;
import com.example.market.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	/**
	 * 지정된 상태에 해당하는 상품을 페이지네이션하여 조회한다.
	 * seller 를 EntityGraph 로 함께 조회하여 목록 변환 시 N+1 을 방지한다.
	 */
	@EntityGraph(attributePaths = "seller")
	Page<Product> findByStatusIn(Collection<ProductStatus> statuses, Pageable pageable);
}
