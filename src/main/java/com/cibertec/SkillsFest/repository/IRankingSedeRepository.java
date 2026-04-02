package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.RankingSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IRankingSedeRepository extends JpaRepository<RankingSede,Long> {
}
