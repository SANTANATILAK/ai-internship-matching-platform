package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.AtsResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtsResultRepository extends JpaRepository<AtsResult, Long> {
    List<AtsResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<AtsResult> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
