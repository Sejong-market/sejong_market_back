package com.example.market.entity;

import com.example.market.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "productid")
	private Integer productId;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ProductStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sellerid", referencedColumnName = "userid", nullable = false)
	private User seller;

	@Column(name = "image_url", length = 225)
	private String imageUrl;

	@Builder
	private Product(String title, String content, Integer price, User seller, String imageUrl) {
		this.title = title;
		this.content = content;
		this.price = price;
		this.seller = seller;
		this.imageUrl = imageUrl;
		this.status = ProductStatus.FOR_SALE;
	}

	public void markReserved() {
		if (this.status == ProductStatus.SOLD_OUT) {
			throw new IllegalStateException("이미 판매 완료된 상품입니다.");
		}
		this.status = ProductStatus.RESERVED;
	}

	public void markSoldOut() {
		this.status = ProductStatus.SOLD_OUT;
	}

	public void markForSale() {
		if (this.status == ProductStatus.SOLD_OUT) {
			throw new IllegalStateException("이미 판매 완료된 상품은 다시 판매중으로 되돌릴 수 없습니다.");
		}
		this.status = ProductStatus.FOR_SALE;
	}
}
