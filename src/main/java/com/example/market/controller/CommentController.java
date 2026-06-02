package com.example.market.controller;

import com.example.market.dto.comment.CommentRequestDto;
import com.example.market.dto.comment.CommentResponseDto;
import com.example.market.entity.User;
import com.example.market.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 * 댓글 작성.
	 *
	 * 인증된 사용자만 가능하며, 로그인 유저 정보는 @AuthenticationPrincipal 로 바로 주입받는다.
	 * SecurityConfig 에서 .authenticated() 로 등록되어 있어야 한다. (공지 2️⃣)
	 *
	 * 예시: POST /api/comments
	 *   { "productId": 1, "content": "직거래 가능한가요?" }
	 */
	@PostMapping
	public ResponseEntity<CommentResponseDto> create(
			@AuthenticationPrincipal User user,
			@Valid @RequestBody CommentRequestDto requestDto) {

		CommentResponseDto response = commentService.createComment(requestDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
