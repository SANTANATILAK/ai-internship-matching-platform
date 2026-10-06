package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    Optional<Skill> findByNormalizedNameIgnoreCase(String normalizedName);
    List<Skill> findByCategory(String category);
}
