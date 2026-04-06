package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {
    List<CriterioEvaluacion> findByEventoId(Long eventoId);
    Page<CriterioEvaluacion> findByEventoId(Long eventoId, Pageable pageable);
}
