package com.winediary.service;

import com.winediary.dto.WineDto;
import com.winediary.entity.Wine;
import com.winediary.entity.WineType;
import com.winediary.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WineService {

    private final WineRepository wineRepository;

    @Transactional
    public Wine createWine(WineDto.Request.Create request) {
        // 중복 체크
        List<Wine> existingWines = wineRepository.findByNameAndProducer(
                request.getName(), request.getProducer());

        if (!existingWines.isEmpty()) {
            return existingWines.get(0); // 기존 와인 반환
        }

        Wine wine = Wine.builder()
                .name(request.getName())
                .type(request.getType())
                .region(request.getRegion())
                .producer(request.getProducer())
                .vintageYear(request.getVintageYear())
                .alcoholContent(request.getAlcoholContent())
                .grapeVarieties(request.getGrapeVarieties())
                .build();

        return wineRepository.save(wine);
    }

    public List<WineDto> searchWines(String keyword) {
        List<Wine> wines = wineRepository.searchWinesByKeyword(keyword);
        return wines.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<WineDto> getWinesByType(WineType type) {
        List<Wine> wines = wineRepository.findByWineType(type);
        return wines.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<WineDto.Response.Summary> getPopularWines() {
        List<Object[]> results = wineRepository.findPopularWines();
        return results.stream()
                .map(result -> {
                    Wine wine = (Wine) result[0];
                    Long noteCount = ((Number) result[1]).longValue();

                    return WineDto.Response.Summary.builder()
                            .wineId(wine.getWineId())
                            .name(wine.getName())
                            .type(wine.getType().getDisplayName())
                            .region(wine.getRegion())
                            .producer(wine.getProducer())
                            .totalNotes(noteCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<WineDto.Response.Summary> getHighRatedWines(double minRating) {
        List<Object[]> results = wineRepository.findHighRatedWines(minRating);
        return results.stream()
                .map(result -> {
                    Wine wine = (Wine) result[0];
                    Double avgRating = (Double) result[1];

                    return WineDto.Response.Summary.builder()
                            .wineId(wine.getWineId())
                            .name(wine.getName())
                            .type(wine.getType().getDisplayName())
                            .region(wine.getRegion())
                            .producer(wine.getProducer())
                            .averageRating(avgRating)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Optional<Wine> findById(Long wineId) {
        return wineRepository.findById(wineId);
    }

    public WineDto convertToDto(Wine wine) {
        return WineDto.builder()
                .wineId(wine.getWineId())
                .name(wine.getName())
                .type(wine.getType())
                .region(wine.getRegion())
                .producer(wine.getProducer())
                .vintageYear(wine.getVintageYear())
                .alcoholContent(wine.getAlcoholContent())
                .grapeVarieties(wine.getGrapeVarieties())
                .createdAt(wine.getCreatedAt())
                .build();
    }
}