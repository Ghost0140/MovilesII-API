package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IResultadoRepository extends JpaRepository<Resultado,Long> {
}
