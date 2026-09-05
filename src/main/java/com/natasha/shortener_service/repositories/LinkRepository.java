package com.natasha.shortener_service.repositories;

import com.natasha.shortener_service.models.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Modifying
    @Query("UPDATE Link SET clicks = clicks + 1 WHERE shortCode = :shortCode")
    void incrementClicks(String shortCode);
}
