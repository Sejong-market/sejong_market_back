package com.example.market.controller;

import com.example.market.dto.product.ProductResponseDto;
import com.example.market.dto.user.UserRequestDto;
import com.example.market.entity.User;
import com.example.market.global.security.JwtUtil;
import com.example.market.repository.UserRepository;
import com.example.market.service.ProductService;
import com.example.market.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

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
     * 내가 등록한 상품 목록을 조회합니다.
     */
    @GetMapping("/mypage/products")
    public ResponseEntity<Page<ProductResponseDto>> getMyProducts(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        try {
            String token = jwtUtil.getJwtFromHeader(request);

            if (token != null && jwtUtil.validateToken(token)) {
                Claims info = jwtUtil.getUserInfoFromToken(token);
                String email = info.getSubject();

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                Page<ProductResponseDto> response = productService.getMyProducts(user, pageable);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(401).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
