package com.example.market.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 작성 요청 DTO.
 *
 * 클라이언트가 어느 상품에 댓글을 다는지 productId 로 전달한다.
 * 작성자는 토큰에서 추출하므로 본문에 받지 않는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {

	@NotNull(message = "상품 ID는 필수입니다.")
	private Integer productId;

	@NotBlank(message = "내용은 필수 입력값입니다.")
	@Size(max = 1_000, message = "내용은 1,000자 이내로 입력해 주세요.")
	private String content;
}
