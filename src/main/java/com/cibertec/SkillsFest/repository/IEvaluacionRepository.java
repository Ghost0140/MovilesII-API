package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IEvaluacionRepository extends JpaRepository<Evaluacion,Long> {
}
