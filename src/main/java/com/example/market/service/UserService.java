package com.example.market.service;

import com.example.market.dto.user.UserRequestDto;
import com.example.market.entity.User;
import com.example.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void signup(UserRequestDto requestDto) {

        Optional<User> found = userRepository.findByEmail(requestDto.getEmail());
        if (found.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(
            requestDto.getEmail(),
            requestDto.getPassword(),
            requestDto.getNickname()
        );

        userRepository.save(user);
    }
}
