package com.winediary.controller;

import com.winediary.dto.ApiResponse;
import com.winediary.dto.UserDto;
import com.winediary.entity.User;
import com.winediary.service.JwtService;
import com.winediary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto.Response.Login>> login(@RequestBody UserDto.Request.Login request) {
        try {
            Optional<User> userOpt = userService.findByProviderAndProviderId(
                    request.getProvider(), request.getProviderId());

            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                // 새 사용자 생성
                UserDto.Request.SignUp signUpRequest = UserDto.Request.SignUp.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .provider(request.getProvider())
                        .providerId(request.getProviderId())
                        .build();

                user = userService.createUser(signUpRequest);
            }

            String token = jwtService.generateToken(user.getUserId());
            UserDto userDto = userService.convertToDto(user);

            UserDto.Response.Login response = UserDto.Response.Login.builder()
                    .token(token)
                    .user(userDto)
                    .build();

            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("로그인 실패", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = jwtService.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success(isValid));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(false));
        }
    }
}