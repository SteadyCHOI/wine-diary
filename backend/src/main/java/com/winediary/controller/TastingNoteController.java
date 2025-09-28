package com.winediary.controller;

import com.winediary.dto.ApiResponse;
import com.winediary.dto.TastingNoteDto;
import com.winediary.entity.TastingNote;
import com.winediary.service.JwtService;
import com.winediary.service.TastingNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasting-notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createTastingNote(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TastingNoteDto.Request.Create request) {
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

            TastingNote tastingNote = tastingNoteService.createTastingNote(userId, request);
            return ResponseEntity.ok(ApiResponse.success("테이스팅 노트 저장 성공", tastingNote.getNoteId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 저장 실패", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TastingNoteDto.Response.Summary>>> getMyTastingNotes(
            @RequestHeader("Authorization") String token) {
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

            List<TastingNoteDto.Response.Summary> notes = tastingNoteService.getUserTastingNotes(userId);
            return ResponseEntity.ok(ApiResponse.success(notes));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 조회 실패", e.getMessage()));
        }
    }

    @GetMapping("/my/page")
    public ResponseEntity<ApiResponse<Page<TastingNoteDto.Response.Summary>>> getMyTastingNotesWithPage(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
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

            Pageable pageable = PageRequest.of(page, size);
            Page<TastingNoteDto.Response.Summary> notes = tastingNoteService.getUserTastingNotesWithPage(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(notes));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 조회 실패", e.getMessage()));
        }
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<TastingNoteDto.Response.Detail>> getTastingNoteDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long noteId) {
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

            Optional<TastingNoteDto.Response.Detail> noteDetail = tastingNoteService.getTastingNoteDetail(noteId, userId);
            if (noteDetail.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(noteDetail.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 상세 조회 실패", e.getMessage()));
        }
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse<String>> updateTastingNote(
            @RequestHeader("Authorization") String token,
            @PathVariable Long noteId,
            @Valid @RequestBody TastingNoteDto.Request.Update request) {
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

            tastingNoteService.updateTastingNote(noteId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("테이스팅 노트 수정 성공"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 수정 실패", e.getMessage()));
        }
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<String>> deleteTastingNote(
            @RequestHeader("Authorization") String token,
            @PathVariable Long noteId) {
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

            tastingNoteService.deleteTastingNote(noteId, userId);
            return ResponseEntity.ok(ApiResponse.success("테이스팅 노트 삭제 성공"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테이스팅 노트 삭제 실패", e.getMessage()));
        }
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<ApiResponse<List<TastingNoteDto.Response.Statistics>>> getMonthlyStatistics(
            @RequestHeader("Authorization") String token) {
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

            List<TastingNoteDto.Response.Statistics> stats = tastingNoteService.getMonthlyStats(userId);
            return ResponseEntity.ok(ApiResponse.success(stats));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("월별 통계 조회 실패", e.getMessage()));
        }
    }

    @GetMapping("/preferences/wine-type")
    public ResponseEntity<ApiResponse<List<Object[]>>> getWineTypePreferences(
            @RequestHeader("Authorization") String token) {
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

            List<Object[]> preferences = tastingNoteService.getWineTypePreferences(userId);
            return ResponseEntity.ok(ApiResponse.success(preferences));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("와인 타입 선호도 조회 실패", e.getMessage()));
        }
    }
}