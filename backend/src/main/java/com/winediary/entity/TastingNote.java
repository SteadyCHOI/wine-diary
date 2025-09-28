package com.winediary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasting_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TastingNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine;

    @Column(name = "rating", nullable = false)
    @Min(1) @Max(5)
    private Integer rating;

    @Column(name = "body_level")
    @Min(1) @Max(5)
    private Integer bodyLevel;

    @Column(name = "tannin_level")
    @Min(1) @Max(5)
    private Integer tanninLevel;

    @Column(name = "acidity_level")
    @Min(1) @Max(5)
    private Integer acidityLevel;

    @Column(name = "sweetness_level")
    @Min(1) @Max(5)
    private Integer sweetnessLevel;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tasting_date", nullable = false)
    private LocalDate tastingDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}