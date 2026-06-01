package com.example.market.service;

import com.example.market.dto.product.ProductRequestDto;
import com.example.market.dto.product.ProductResponseDto;
import com.example.market.dto.product.ProductStatusRequestDto;
import com.example.market.entity.Product;
import com.example.market.entity.ProductStatus;
import com.example.market.entity.User;
import com.example.market.global.util.FileStorageService;
import com.example.market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	private final FileStorageService fileStorageService;

	/**
	 * 상품을 등록한다.
	 *
	 * 이미지가 함께 업로드된 경우 파일 시스템에 저장한 뒤 URL 을 DB 에 보관한다.
	 * 상태는 엔티티 생성 시 자동으로 FOR_SALE 로 초기화된다.
	 *
	 * @param requestDto 제목/내용/가격을 담은 요청 DTO
	 * @param image      업로드된 이미지(선택)
	 * @param seller     인증된 판매자 사용자
	 * @return 저장된 상품을 표현하는 응답 DTO
	 */
	@Transactional
	public ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile image, User seller) {
		String imageUrl = fileStorageService.storeProductImage(image);

		Product product = Product.builder()
				.title(requestDto.getTitle())
				.content(requestDto.getContent())
				.price(requestDto.getPrice())
				.seller(seller)
				.imageUrl(imageUrl)
				.build();

		Product saved = productRepository.save(product);
		return ProductResponseDto.from(saved);
	}

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

	/**
	 * 상품 상태 변경.
	 * 판매자 본인만 변경 가능하다.
	 */
	@Transactional
	public void updateStatus(Integer productId, ProductStatusRequestDto requestDto, User user) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

		if (!product.getSeller().getUserId().equals(user.getUserId())) {
			throw new IllegalArgumentException("상품 상태를 변경할 권한이 없습니다.");
		}

		ProductStatus newStatus = requestDto.getStatus();
		switch (newStatus) {
			case FOR_SALE -> product.markForSale();
			case RESERVED -> product.markReserved();
			case SOLD_OUT -> product.markSoldOut();
		}
	}
}
