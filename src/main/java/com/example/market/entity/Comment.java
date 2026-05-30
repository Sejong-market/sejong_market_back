package com.example.market.entity;

import com.example.market.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 엔티티 (ERD `comments` 테이블 매핑).
 *
 * - PK: commentid
 * - FK: writerid → users.userid, productid → products.productid
 * - 생성 시각만 추적하면 되므로 BaseCreatedEntity 를 상속한다 (수정 이력 없음).
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseCreatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "commentid")
	private Integer commentId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "writerid", referencedColumnName = "userid", nullable = false)
	private User writer;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "productid", referencedColumnName = "productid", nullable = false)
	private Product product;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;

	@Builder
	private Comment(User writer, Product product, String content) {
		this.writer = writer;
		this.product = product;
		this.content = content;
	}
}
