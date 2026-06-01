package com.example.market.dto.user;

import com.example.market.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final String email;
    private final String nickname;

    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
