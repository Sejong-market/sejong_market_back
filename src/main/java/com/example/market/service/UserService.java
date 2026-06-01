package com.example.market.service;

import com.example.market.dto.user.UserRequestDto;
import com.example.market.dto.user.UserResponseDto;
import com.example.market.entity.User;
import com.example.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(UserRequestDto requestDto) {

        Optional<User> found = userRepository.findByEmail(requestDto.getEmail());
        if (found.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(
            requestDto.getEmail(),
            encodedPassword,
            requestDto.getNickname()
        );

        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public User login(UserRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(User user) {
        return new UserResponseDto(user);
    }
}