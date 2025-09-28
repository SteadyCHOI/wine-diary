package com.winediary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private String provider;
    private LocalDateTime createdAt;

    public static class Request {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SignUp {
            private String name;
            private String email;
            private String provider;
            private String providerId;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Login {
            private String provider;
            private String providerId;
            private String name;
            private String email;
        }
    }

    public static class Response {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Profile {
            private Long userId;
            private String name;
            private String email;
            private String provider;
            private LocalDateTime createdAt;
            private Long totalNotes;
            private Double averageRating;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Login {
            private String token;
            private UserDto user;
        }
    }
}