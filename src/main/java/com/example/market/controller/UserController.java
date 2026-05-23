package com.example.market.controller;

import com.example.market.dto.user.UserRequestDto;
import com.example.market.entity.User;
import com.example.market.global.security.JwtUtil;
import com.example.market.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@RequestBody UserRequestDto requestDto) {
        try {
            userService.signUp(requestDto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody UserRequestDto requestDto,
            HttpServletResponse response) {
        try {
            User user = userService.login(requestDto);

            String token = jwtUtil.createToken(user.getEmail());

            response.addHeader(jwtUtil.AUTHORIZATION_HEADER, token);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
