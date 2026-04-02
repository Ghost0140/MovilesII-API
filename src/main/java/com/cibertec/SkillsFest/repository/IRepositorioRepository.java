package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRepositorioRepository extends JpaRepository<Repositorio,Long> {
}
