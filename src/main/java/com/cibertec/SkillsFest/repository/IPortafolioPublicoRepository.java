package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.PortafolioPublico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPortafolioPublicoRepository extends JpaRepository<PortafolioPublico, Long> {
    Optional<PortafolioPublico> findByUsuarioIdAndActivoTrue(Long usuarioId);
    Optional<PortafolioPublico> findBySlugAndActivoTrue(String slug);
}