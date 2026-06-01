package com.example.market.controller;

import com.example.market.dto.product.ProductResponseDto;
import com.example.market.dto.user.UserRequestDto;
import com.example.market.dto.user.UserResponseDto;
import com.example.market.dto.user.UserUpdateRequestDto;
import com.example.market.entity.User;
import com.example.market.global.security.JwtUtil;
import com.example.market.service.ProductService;
import com.example.market.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;
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

    /**
     * 내 정보를 조회합니다.
     */
    @GetMapping("/mypage")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponseDto responseDto = userService.getUserInfo(user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 내 정보를 수정합니다.
     */
    @PatchMapping("/mypage")
    public ResponseEntity<Void> updateUserInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateRequestDto requestDto) {
        try {
            userService.updateUserInfo(user, requestDto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 내가 등록한 상품 목록을 조회합니다.
     */
    @GetMapping("/mypage/products")
    public ResponseEntity<Page<ProductResponseDto>> getMyProducts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductResponseDto> response = productService.getMyProducts(user, pageable);
        return ResponseEntity.ok(response);
    }
}
