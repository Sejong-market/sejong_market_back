package com.example.market.repository;

import com.example.market.entity.Comment;
import com.example.market.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

	/**
	 * 특정 상품에 달린 댓글을 생성 시각 오름차순(오래된 순)으로 조회한다.
	 * writer 를 EntityGraph 로 함께 로딩하여 닉네임 변환 시 N+1 을 방지한다.
	 */
	@EntityGraph(attributePaths = "writer")
	List<Comment> findByProductOrderByCreatedAtAsc(Product product);
}
