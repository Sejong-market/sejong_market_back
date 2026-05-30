package com.example.market.dto.product;

import com.example.market.dto.comment.CommentResponseDto;
import com.example.market.entity.Product;
import com.example.market.entity.ProductStatus;
import com.example.market.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 상세 페이지 응답 DTO.
 *
 * 목록 응답과 달리 본문(content), 수정 시각, 판매자 식별자, 댓글 목록까지 포함한다.
 * - isMine: 현재 로그인 유저가 판매자인지 여부 (수정/삭제/상태변경 버튼 노출 판단용)
 * - seller: 판매자 식별자/닉네임 (이메일/비밀번호 등 민감정보는 절대 포함하지 않는다)
 * - comments: 생성 순(오래된 순)으로 정렬된 댓글 목록
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDetailResponseDto {

	private final Integer productId;
	private final String title;
	private final String content;
	private final Integer price;
	private final ProductStatus status;
	private final String imageUrl;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private final SellerInfo seller;
	private final boolean isMine;

	private final int commentCount;
	private final List<CommentResponseDto> comments;

	/**
	 * 상품 + 댓글 목록 + 현재 로그인 사용자(nullable) 를 받아 상세 DTO 를 조립한다.
	 */
	public static ProductDetailResponseDto of(
			Product product,
			User currentUser,
			List<CommentResponseDto> comments) {

		User sellerUser = product.getSeller();
		boolean mine = currentUser != null
				&& sellerUser.getUserId().equals(currentUser.getUserId());

		return ProductDetailResponseDto.builder()
				.productId(product.getProductId())
				.title(product.getTitle())
				.content(product.getContent())
				.price(product.getPrice())
				.status(product.getStatus())
				.imageUrl(product.getImageUrl())
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.seller(new SellerInfo(sellerUser.getUserId(), sellerUser.getNickname()))
				.isMine(mine)
				.commentCount(comments.size())
				.comments(comments)
				.build();
	}

	@Getter
	@AllArgsConstructor
	public static class SellerInfo {
		private final Integer sellerId;
		private final String nickname;
	}
}
