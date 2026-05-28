package com.example.market.service;

import com.example.market.dto.product.ProductResponseDto;
import com.example.market.entity.ProductStatus;
import com.example.market.entity.User;
import com.example.market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	/** 목록 페이지에서 기본으로 노출되는 상태 (SOLD_OUT 은 숨김). */
	private static final Set<ProductStatus> VISIBLE_STATUSES =
			EnumSet.of(ProductStatus.FOR_SALE, ProductStatus.RESERVED);

	private final ProductRepository productRepository;

	/**
	 * 상품 목록 조회. SOLD_OUT 상태는 기본 노출에서 제외한다.
	 *
	 * @param pageable 페이지/사이즈/정렬 정보 (예: page=0&size=20&sort=createdAt,desc)
	 */
	public Page<ProductResponseDto> findAll(Pageable pageable) {
		return productRepository
				.findByStatusIn(VISIBLE_STATUSES, pageable)
				.map(ProductResponseDto::from);
	}

	/**
	 * 특정 사용자가 등록한 상품 목록 조회.
	 *
	 * @param user 조회할 사용자 엔티티
	 * @param pageable 페이지네이션 정보
	 */
	public Page<ProductResponseDto> getMyProducts(User user, Pageable pageable) {
		return productRepository
				.findBySeller(user, pageable)
				.map(ProductResponseDto::from);
	}
}
