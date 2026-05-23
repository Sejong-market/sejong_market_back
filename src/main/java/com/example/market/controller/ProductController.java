package com.example.market.controller;

import com.example.market.dto.product.ProductResponseDto;
import com.example.market.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	/**
	 * 상품 목록 조회.
	 *
	 * 기본 정렬은 최신 등록순(createdAt 내림차순) 이며,
	 * SOLD_OUT 상태의 상품은 노출되지 않는다.
	 * 비로그인 사용자도 접근 가능하다.
	 *
	 * 예시: GET /api/products?page=0&size=20
	 */
	@GetMapping
	public ResponseEntity<Page<ProductResponseDto>> list(
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(productService.findAll(pageable));
	}
}
