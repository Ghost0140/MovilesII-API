package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISedeRepository extends JpaRepository<Sede, Long> {
    Optional<Sede> findByCodigo(String codigo);
    List<Sede> findByActivoTrue();
}