package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.RankingArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRankingAreaRepository extends JpaRepository<RankingArea, Long> {
    List<RankingArea> findByEventoId(Long eventoId);
    List<RankingArea> findByEventoIdAndArea(Long eventoId, String area);
    Page<RankingArea> findByEventoIdAndArea(Long eventoId, String area, Pageable pageable);
    Optional<RankingArea> findByUsuarioIdAndEventoIdAndArea(Long usuarioId, Long eventoId, String area);
}
