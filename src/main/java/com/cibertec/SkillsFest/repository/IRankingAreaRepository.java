package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.RankingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IRankingAreaRepository extends JpaRepository<RankingArea,Long> {
}
