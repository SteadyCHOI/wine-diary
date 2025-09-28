package com.winediary.repository;

import com.winediary.entity.Wine;
import com.winediary.entity.WineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {

    @Query("SELECT w FROM Wine w " +
           "WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(w.producer) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(w.region) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Wine> searchWinesByKeyword(@Param("keyword") String keyword);

    @Query("SELECT w, COUNT(tn) as noteCount " +
           "FROM Wine w " +
           "LEFT JOIN w.tastingNotes tn " +
           "GROUP BY w " +
           "ORDER BY noteCount DESC")
    List<Object[]> findPopularWines();

    @Query("SELECT w, AVG(tn.rating) as avgRating " +
           "FROM Wine w " +
           "JOIN w.tastingNotes tn " +
           "GROUP BY w " +
           "HAVING AVG(tn.rating) >= :minRating " +
           "ORDER BY avgRating DESC")
    List<Object[]> findHighRatedWines(@Param("minRating") Double minRating);

    @Query("SELECT w FROM Wine w WHERE w.type = :wineType ORDER BY w.name")
    List<Wine> findByWineType(@Param("wineType") WineType wineType);

    @Query("SELECT w FROM Wine w WHERE w.name = :name AND w.producer = :producer")
    List<Wine> findByNameAndProducer(@Param("name") String name, @Param("producer") String producer);
}