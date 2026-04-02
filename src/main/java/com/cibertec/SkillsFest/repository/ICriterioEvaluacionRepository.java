package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ICriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion,Long> {
}
