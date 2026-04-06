package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.RankingSede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRankingSedeRepository extends JpaRepository<RankingSede, Long> {
    List<RankingSede> findByEventoId(Long eventoId);
}