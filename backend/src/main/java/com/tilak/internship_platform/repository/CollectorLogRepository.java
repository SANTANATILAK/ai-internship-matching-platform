package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.CollectorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectorLogRepository extends JpaRepository<CollectorLog, Long> {
    List<CollectorLog> findTop20ByOrderByStartedAtDesc();
}
