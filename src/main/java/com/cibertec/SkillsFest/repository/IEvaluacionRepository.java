package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Evaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByProyectoId(Long proyectoId);
    Page<Evaluacion> findByProyectoId(Long proyectoId, Pageable pageable);
    List<Evaluacion> findByJuradoId(Long juradoId);
    boolean existsByProyectoIdAndJuradoIdAndCriterioId(Long proyectoId, Long juradoId, Long criterioId);
}
