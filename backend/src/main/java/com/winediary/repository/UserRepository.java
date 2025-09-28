package com.winediary.repository;

import com.winediary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId")
    Optional<User> findByProviderAndProviderId(@Param("provider") String provider,
                                               @Param("providerId") String providerId);

    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.tastingNotes tn " +
           "WHERE tn.createdAt >= :since")
    List<User> findActiveUsersSince(@Param("since") LocalDateTime since);
}