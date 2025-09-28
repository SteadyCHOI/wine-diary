package com.winediary.repository;

import com.winediary.entity.TastingNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingNoteRepository extends JpaRepository<TastingNote, Long> {

    @Query("SELECT tn FROM TastingNote tn " +
           "JOIN FETCH tn.wine " +
           "WHERE tn.user.userId = :userId " +
           "ORDER BY tn.createdAt DESC")
    List<TastingNote> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT tn FROM TastingNote tn " +
           "JOIN FETCH tn.wine " +
           "WHERE tn.user.userId = :userId " +
           "ORDER BY tn.createdAt DESC")
    Page<TastingNote> findByUserIdOrderByCreatedAtDescWithPage(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT tn FROM TastingNote tn " +
           "JOIN FETCH tn.user " +
           "WHERE tn.wine.wineId = :wineId " +
           "ORDER BY tn.createdAt DESC")
    List<TastingNote> findByWineIdWithUser(@Param("wineId") Long wineId);

    @Query("SELECT tn FROM TastingNote tn " +
           "JOIN FETCH tn.wine " +
           "WHERE tn.rating >= :minRating " +
           "ORDER BY tn.rating DESC, tn.createdAt DESC")
    List<TastingNote> findByRatingGreaterThanEqual(@Param("minRating") Integer minRating);

    @Query("SELECT AVG(tn.rating) FROM TastingNote tn " +
           "WHERE tn.user.userId = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);

    @Query("SELECT DATE_FORMAT(tn.tastingDate, '%Y-%m') as month, COUNT(tn) " +
           "FROM TastingNote tn " +
           "WHERE tn.user.userId = :userId " +
           "GROUP BY DATE_FORMAT(tn.tastingDate, '%Y-%m') " +
           "ORDER BY month DESC")
    List<Object[]> findMonthlyTastingStats(@Param("userId") Long userId);

    @Query("SELECT w.type, AVG(tn.rating), COUNT(tn) " +
           "FROM TastingNote tn " +
           "JOIN tn.wine w " +
           "WHERE tn.user.userId = :userId " +
           "GROUP BY w.type " +
           "ORDER BY AVG(tn.rating) DESC")
    List<Object[]> findWineTypePreferences(@Param("userId") Long userId);

    @Query("SELECT COUNT(tn) FROM TastingNote tn WHERE tn.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}