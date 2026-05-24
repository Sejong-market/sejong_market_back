package com.example.market.entity;

import com.example.market.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseCreatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userid")
	private Long userId;

	@Column(name = "email", nullable = false, unique = true, length = 45)
	private String email;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "nickname", nullable = false, length = 45)
	private String nickname;

	@Builder
	private User(String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}
}
