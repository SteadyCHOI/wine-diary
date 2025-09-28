package com.winediary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wine_id")
    private Long wineId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private WineType type;

    @Column(name = "region", length = 200)
    private String region;

    @Column(name = "producer", length = 200)
    private String producer;

    @Column(name = "vintage_year")
    private Integer vintageYear;

    @Column(name = "alcohol_content", precision = 4, scale = 2)
    private BigDecimal alcoholContent;

    @Column(name = "grape_varieties", length = 500)
    private String grapeVarieties;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wine", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TastingNote> tastingNotes = new ArrayList<>();
}