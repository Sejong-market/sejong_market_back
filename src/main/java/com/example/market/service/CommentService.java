package com.example.market.service;

import com.example.market.dto.comment.CommentRequestDto;
import com.example.market.dto.comment.CommentResponseDto;
import com.example.market.entity.Comment;
import com.example.market.entity.Product;
import com.example.market.entity.User;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final ProductRepository productRepository;

	/**
	 * 댓글을 작성한다.
	 *
	 * 인증된 사용자가 작성자(writer) 로 자동 지정된다. (Spring Security 가이드 1️⃣)
	 *
	 * @param requestDto 상품 ID, 본문
	 * @param writer     로그인한 사용자 (@AuthenticationPrincipal)
	 */
	@Transactional
	public CommentResponseDto createComment(CommentRequestDto requestDto, User writer) {
		Product product = productRepository.findById(requestDto.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

		Comment comment = Comment.builder()
				.writer(writer)
				.product(product)
				.content(requestDto.getContent())
				.build();

		Comment saved = commentRepository.save(comment);
		return CommentResponseDto.from(saved, writer);
	}

	/**
	 * 특정 상품의 댓글 목록을 생성 시각 오름차순으로 조회한다.
	 *
	 * @param product     댓글이 달린 상품
	 * @param currentUser 현재 로그인한 사용자 (비로그인 시 null) — 각 댓글의 isMine 판별에 사용
	 */
	public List<CommentResponseDto> getCommentsByProduct(Product product, User currentUser) {
		return commentRepository.findByProductOrderByCreatedAtAsc(product).stream()
				.map(c -> CommentResponseDto.from(c, currentUser))
				.toList();
	}
}
