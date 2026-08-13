package com.natasha.analytics_service.repository;

import com.natasha.analytics_service.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    int countByShortCode(String shortCode);
}
