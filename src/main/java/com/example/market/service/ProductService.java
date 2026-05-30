package com.example.market.service;

import com.example.market.dto.comment.CommentResponseDto;
import com.example.market.dto.product.ProductDetailResponseDto;
import com.example.market.dto.product.ProductRequestDto;
import com.example.market.dto.product.ProductResponseDto;
import com.example.market.dto.product.ProductStatusRequestDto;
import com.example.market.entity.Product;
import com.example.market.entity.ProductStatus;
import com.example.market.entity.User;
import com.example.market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	/** 목록 페이지에서 기본으로 노출되는 상태 (SOLD_OUT 은 숨김). */
	private static final Set<ProductStatus> VISIBLE_STATUSES =
			EnumSet.of(ProductStatus.FOR_SALE, ProductStatus.RESERVED);

	private final ProductRepository productRepository;
	private final CommentService commentService;

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
	 * 상품 상세 조회.
	 *
	 * 본문(content), 판매자 정보, 댓글 목록까지 함께 반환한다.
	 * 비로그인 사용자도 호출 가능하며, 이 경우 currentUser 가 null 로 들어와
	 * isMine 값은 모두 false 가 된다.
	 *
	 * @param productId   조회할 상품 ID
	 * @param currentUser 현재 로그인 사용자 (비로그인 시 null)
	 */
	public ProductDetailResponseDto getDetail(Integer productId, User currentUser) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

		List<CommentResponseDto> comments =
				commentService.getCommentsByProduct(product, currentUser);

		return ProductDetailResponseDto.of(product, currentUser, comments);
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
