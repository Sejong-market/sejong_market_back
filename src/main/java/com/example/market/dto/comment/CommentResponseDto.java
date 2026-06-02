package com.example.market.dto.comment;

import com.example.market.entity.Comment;
import com.example.market.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO.
 *
 * 작성자 정보는 닉네임만 노출하여 민감 정보(이메일/비밀번호) 누출을 방지한다.
 * isMine 필드를 함께 내려주어 클라이언트가 본인 댓글 여부를 손쉽게 판별할 수 있도록 한다.
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {

	private final Integer commentId;
	private final String content;
	private final Integer writerId;
	private final String writerNickname;
	private final LocalDateTime createdAt;
	private final boolean isMine;

	/**
	 * @param comment       원본 댓글 엔티티
	 * @param currentUser   현재 로그인한 사용자 (비로그인 시 null 허용)
	 */
	public static CommentResponseDto from(Comment comment, User currentUser) {
		User writer = comment.getWriter();
		boolean mine = currentUser != null
				&& writer.getUserId().equals(currentUser.getUserId());

		return CommentResponseDto.builder()
				.commentId(comment.getCommentId())
				.content(comment.getContent())
				.writerId(writer.getUserId())
				.writerNickname(writer.getNickname())
				.createdAt(comment.getCreatedAt())
				.isMine(mine)
				.build();
	}
}
