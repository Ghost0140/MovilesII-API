package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRepositorioRepository extends JpaRepository<Repositorio, Long> {
    Optional<Repositorio> findByProyectoId(Long proyectoId);
    List<Repositorio> findByProyectoIdIn(List<Long> proyectoIds);
}
