package com.example.market.controller;

import com.example.market.dto.product.ProductRequestDto;
import com.example.market.dto.product.ProductResponseDto;
import com.example.market.dto.product.ProductStatusRequestDto;
import com.example.market.entity.User;
import com.example.market.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	/**
	 * 상품 등록.
	 *
	 * 인증된 사용자만 등록할 수 있으며, 제목/내용/가격을 JSON 파트(`request`)로 전달하고
	 * 이미지 파일은 선택적으로 `image` 파트로 함께 업로드한다.
	 *
	 * 예시: POST /api/products  (Content-Type: multipart/form-data)
	 *   - request : application/json   { "title": "...", "content": "...", "price": 10000 }
	 *   - image   : (선택) 이미지 파일
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProductResponseDto> create(
			@Valid @RequestPart("request") ProductRequestDto requestDto,
			@RequestPart(value = "image", required = false) MultipartFile image,
			@AuthenticationPrincipal User user) {

		ProductResponseDto response = productService.createProduct(requestDto, image, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

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

	/**
	 * 상품 상태 변경.
	 * 판매자 본인만 변경 가능하다.
	 */
	@PatchMapping("/{productId}/status")
	public ResponseEntity<Void> updateStatus(
			@PathVariable Integer productId,
			@RequestBody ProductStatusRequestDto requestDto,
			@AuthenticationPrincipal User user) {

		productService.updateStatus(productId, requestDto, user);
		return ResponseEntity.ok().build();
	}
}
