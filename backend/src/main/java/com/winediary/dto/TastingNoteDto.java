package com.winediary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TastingNoteDto {
    private Long noteId;
    private UserDto user;
    private WineDto wine;
    private Integer rating;
    private Integer bodyLevel;
    private Integer tanninLevel;
    private Integer acidityLevel;
    private Integer sweetnessLevel;
    private String notes;
    private LocalDate tastingDate;
    private LocalDateTime createdAt;

    public static class Request {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Create {
            @NotNull
            private Long wineId;

            @NotNull
            @Min(1) @Max(5)
            private Integer rating;

            @Min(1) @Max(5)
            private Integer bodyLevel;

            @Min(1) @Max(5)
            private Integer tanninLevel;

            @Min(1) @Max(5)
            private Integer acidityLevel;

            @Min(1) @Max(5)
            private Integer sweetnessLevel;

            private String notes;

            @NotNull
            private LocalDate tastingDate;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Update {
            @Min(1) @Max(5)
            private Integer rating;

            @Min(1) @Max(5)
            private Integer bodyLevel;

            @Min(1) @Max(5)
            private Integer tanninLevel;

            @Min(1) @Max(5)
            private Integer acidityLevel;

            @Min(1) @Max(5)
            private Integer sweetnessLevel;

            private String notes;
            private LocalDate tastingDate;
        }
    }

    public static class Response {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Summary {
            private Long noteId;
            private WineDto.Response.Summary wine;
            private Integer rating;
            private String notes;
            private LocalDate tastingDate;
            private LocalDateTime createdAt;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Detail {
            private Long noteId;
            private WineDto.Response.Detail wine;
            private Integer rating;
            private Integer bodyLevel;
            private Integer tanninLevel;
            private Integer acidityLevel;
            private Integer sweetnessLevel;
            private String notes;
            private LocalDate tastingDate;
            private LocalDateTime createdAt;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Statistics {
            private String period;
            private Long count;
            private Double averageRating;
        }
    }
}