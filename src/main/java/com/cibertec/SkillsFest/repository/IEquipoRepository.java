package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IEquipoRepository extends JpaRepository<Equipo,Long> {
}
