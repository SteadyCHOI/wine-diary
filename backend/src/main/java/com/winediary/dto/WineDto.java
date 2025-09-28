package com.winediary.dto;

import com.winediary.entity.WineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineDto {
    private Long wineId;
    private String name;
    private WineType type;
    private String region;
    private String producer;
    private Integer vintageYear;
    private BigDecimal alcoholContent;
    private String grapeVarieties;
    private LocalDateTime createdAt;

    public static class Request {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Create {
            private String name;
            private WineType type;
            private String region;
            private String producer;
            private Integer vintageYear;
            private BigDecimal alcoholContent;
            private String grapeVarieties;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Search {
            private String keyword;
            private WineType type;
            private String region;
        }
    }

    public static class Response {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Summary {
            private Long wineId;
            private String name;
            private String type;
            private String region;
            private String producer;
            private Double averageRating;
            private Long totalNotes;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Detail {
            private Long wineId;
            private String name;
            private WineType type;
            private String region;
            private String producer;
            private Integer vintageYear;
            private BigDecimal alcoholContent;
            private String grapeVarieties;
            private Double averageRating;
            private Long totalNotes;
            private LocalDateTime createdAt;
        }
    }
}