package com.winediary.controller;

import com.winediary.dto.ApiResponse;
import com.winediary.dto.WineDto;
import com.winediary.entity.Wine;
import com.winediary.entity.WineType;
import com.winediary.service.JwtService;
import com.winediary.service.WineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WineController {

    private final WineService wineService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ApiResponse<WineDto>> createWine(
            @RequestHeader("Authorization") String token,
            @RequestBody WineDto.Request.Create request) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("인증 실패"));
            }

            Wine wine = wineService.createWine(request);
            WineDto wineDto = wineService.convertToDto(wine);

            return ResponseEntity.ok(ApiResponse.success("와인 등록 성공", wineDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("와인 등록 실패", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<WineDto>>> searchWines(@RequestParam String keyword) {
        try {
            List<WineDto> wines = wineService.searchWines(keyword);
            return ResponseEntity.ok(ApiResponse.success(wines));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("와인 검색 실패", e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<WineDto>>> getWinesByType(@PathVariable WineType type) {
        try {
            List<WineDto> wines = wineService.getWinesByType(type);
            return ResponseEntity.ok(ApiResponse.success(wines));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("와인 타입별 조회 실패", e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<WineDto.Response.Summary>>> getPopularWines() {
        try {
            List<WineDto.Response.Summary> wines = wineService.getPopularWines();
            return ResponseEntity.ok(ApiResponse.success(wines));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인기 와인 조회 실패", e.getMessage()));
        }
    }

    @GetMapping("/high-rated")
    public ResponseEntity<ApiResponse<List<WineDto.Response.Summary>>> getHighRatedWines(
            @RequestParam(defaultValue = "4.0") double minRating) {
        try {
            List<WineDto.Response.Summary> wines = wineService.getHighRatedWines(minRating);
            return ResponseEntity.ok(ApiResponse.success(wines));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고평점 와인 조회 실패", e.getMessage()));
        }
    }
}