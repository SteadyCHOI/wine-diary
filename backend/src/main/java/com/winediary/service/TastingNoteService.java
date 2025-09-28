package com.winediary.service;

import com.winediary.dto.TastingNoteDto;
import com.winediary.dto.WineDto;
import com.winediary.entity.TastingNote;
import com.winediary.entity.User;
import com.winediary.entity.Wine;
import com.winediary.repository.TastingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TastingNoteService {

    private final TastingNoteRepository tastingNoteRepository;
    private final UserService userService;
    private final WineService wineService;

    @Transactional
    public TastingNote createTastingNote(Long userId, TastingNoteDto.Request.Create request) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wine wine = wineService.findById(request.getWineId())
                .orElseThrow(() -> new RuntimeException("Wine not found"));

        TastingNote tastingNote = TastingNote.builder()
                .user(user)
                .wine(wine)
                .rating(request.getRating())
                .bodyLevel(request.getBodyLevel())
                .tanninLevel(request.getTanninLevel())
                .acidityLevel(request.getAcidityLevel())
                .sweetnessLevel(request.getSweetnessLevel())
                .notes(request.getNotes())
                .tastingDate(request.getTastingDate())
                .build();

        return tastingNoteRepository.save(tastingNote);
    }

    @Transactional
    public TastingNote updateTastingNote(Long noteId, Long userId, TastingNoteDto.Request.Update request) {
        TastingNote tastingNote = tastingNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Tasting note not found"));

        if (!tastingNote.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this note");
        }

        if (request.getRating() != null) {
            tastingNote.setRating(request.getRating());
        }
        if (request.getBodyLevel() != null) {
            tastingNote.setBodyLevel(request.getBodyLevel());
        }
        if (request.getTanninLevel() != null) {
            tastingNote.setTanninLevel(request.getTanninLevel());
        }
        if (request.getAcidityLevel() != null) {
            tastingNote.setAcidityLevel(request.getAcidityLevel());
        }
        if (request.getSweetnessLevel() != null) {
            tastingNote.setSweetnessLevel(request.getSweetnessLevel());
        }
        if (request.getNotes() != null) {
            tastingNote.setNotes(request.getNotes());
        }
        if (request.getTastingDate() != null) {
            tastingNote.setTastingDate(request.getTastingDate());
        }

        return tastingNoteRepository.save(tastingNote);
    }

    @Transactional
    public void deleteTastingNote(Long noteId, Long userId) {
        TastingNote tastingNote = tastingNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Tasting note not found"));

        if (!tastingNote.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this note");
        }

        tastingNoteRepository.delete(tastingNote);
    }

    public List<TastingNoteDto.Response.Summary> getUserTastingNotes(Long userId) {
        List<TastingNote> notes = tastingNoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notes.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    public Page<TastingNoteDto.Response.Summary> getUserTastingNotesWithPage(Long userId, Pageable pageable) {
        Page<TastingNote> notes = tastingNoteRepository.findByUserIdOrderByCreatedAtDescWithPage(userId, pageable);
        return notes.map(this::convertToSummaryDto);
    }

    public Optional<TastingNoteDto.Response.Detail> getTastingNoteDetail(Long noteId, Long userId) {
        Optional<TastingNote> noteOpt = tastingNoteRepository.findById(noteId);
        if (noteOpt.isPresent()) {
            TastingNote note = noteOpt.get();
            if (note.getUser().getUserId().equals(userId)) {
                return Optional.of(convertToDetailDto(note));
            }
        }
        return Optional.empty();
    }

    public List<TastingNoteDto.Response.Statistics> getMonthlyStats(Long userId) {
        List<Object[]> results = tastingNoteRepository.findMonthlyTastingStats(userId);
        return results.stream()
                .map(result -> TastingNoteDto.Response.Statistics.builder()
                        .period((String) result[0])
                        .count(((Number) result[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<Object[]> getWineTypePreferences(Long userId) {
        return tastingNoteRepository.findWineTypePreferences(userId);
    }

    private TastingNoteDto.Response.Summary convertToSummaryDto(TastingNote note) {
        return TastingNoteDto.Response.Summary.builder()
                .noteId(note.getNoteId())
                .wine(WineDto.Response.Summary.builder()
                        .wineId(note.getWine().getWineId())
                        .name(note.getWine().getName())
                        .type(note.getWine().getType().getDisplayName())
                        .region(note.getWine().getRegion())
                        .producer(note.getWine().getProducer())
                        .build())
                .rating(note.getRating())
                .notes(note.getNotes())
                .tastingDate(note.getTastingDate())
                .createdAt(note.getCreatedAt())
                .build();
    }

    private TastingNoteDto.Response.Detail convertToDetailDto(TastingNote note) {
        return TastingNoteDto.Response.Detail.builder()
                .noteId(note.getNoteId())
                .wine(WineDto.Response.Detail.builder()
                        .wineId(note.getWine().getWineId())
                        .name(note.getWine().getName())
                        .type(note.getWine().getType())
                        .region(note.getWine().getRegion())
                        .producer(note.getWine().getProducer())
                        .vintageYear(note.getWine().getVintageYear())
                        .alcoholContent(note.getWine().getAlcoholContent())
                        .grapeVarieties(note.getWine().getGrapeVarieties())
                        .createdAt(note.getWine().getCreatedAt())
                        .build())
                .rating(note.getRating())
                .bodyLevel(note.getBodyLevel())
                .tanninLevel(note.getTanninLevel())
                .acidityLevel(note.getAcidityLevel())
                .sweetnessLevel(note.getSweetnessLevel())
                .notes(note.getNotes())
                .tastingDate(note.getTastingDate())
                .createdAt(note.getCreatedAt())
                .build();
    }
}