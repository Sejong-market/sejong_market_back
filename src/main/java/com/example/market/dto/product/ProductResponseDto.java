package com.example.market.dto.product;

import com.example.market.entity.Product;
import com.example.market.entity.ProductStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 목록 페이지 카드에 사용되는 응답 DTO.
 *
 * 목록에서는 본문(content) 같은 큰 필드는 제외하여 페이로드를 줄이고,
 * 판매자 정보는 닉네임만 노출하여 민감 정보(이메일/비밀번호)가 새지 않도록 한다.
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponseDto {

	private final Long productId;
	private final String title;
	private final Integer price;
	private final ProductStatus status;
	private final String imageUrl;
	private final String sellerNickname;
	private final LocalDateTime createdAt;

	public static ProductResponseDto from(Product product) {
		return ProductResponseDto.builder()
				.productId(product.getProductId())
				.title(product.getTitle())
				.price(product.getPrice())
				.status(product.getStatus())
				.imageUrl(product.getImageUrl())
				.sellerNickname(product.getSeller().getNickname())
				.createdAt(product.getCreatedAt())
				.build();
	}
}
