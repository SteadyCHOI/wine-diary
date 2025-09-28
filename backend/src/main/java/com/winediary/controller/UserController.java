package com.winediary.controller;

import com.winediary.dto.ApiResponse;
import com.winediary.dto.UserDto;
import com.winediary.service.JwtService;
import com.winediary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto.Response.Profile>> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String userIdStr = jwtService.extractUserId(token);
            Long userId = Long.parseLong(userIdStr);

            if (!jwtService.validateToken(token, userId)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("인증 실패"));
            }

            UserDto.Response.Profile profile = userService.getUserProfile(userId);
            return ResponseEntity.ok(ApiResponse.success(profile));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("프로필 조회 실패", e.getMessage()));
        }
    }
}