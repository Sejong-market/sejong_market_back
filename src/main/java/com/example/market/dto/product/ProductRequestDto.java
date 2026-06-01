package com.example.market.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 등록/수정 요청 DTO.
 *
 * multipart/form-data 요청 본문 중 JSON/Form 파트로 전달되는 필드를 담는다.
 * 이미지 파일(MultipartFile)은 별도의 파트로 분리하여 Controller 에서 받는다.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProductRequestDto {

	@NotBlank(message = "제목은 필수 입력값입니다.")
	@Size(max = 100, message = "제목은 100자 이내로 입력해 주세요.")
	private String title;

	@Size(max = 10_000, message = "내용은 10,000자 이내로 입력해 주세요.")
	private String content;

	@NotNull(message = "가격은 필수 입력값입니다.")
	@PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
	private Integer price;
}
