package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.PortafolioPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPortafolioPublicoRepository extends JpaRepository<PortafolioPublico, Long> {
    Optional<PortafolioPublico> findByUsuarioId(Long usuarioId);
    Optional<PortafolioPublico> findBySlug(String slug);
}
