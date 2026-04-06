package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Resultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IResultadoRepository extends JpaRepository<Resultado, Long> {
    List<Resultado> findByEventoId(Long eventoId);
    Page<Resultado> findByEventoId(Long eventoId, Pageable pageable);
    Optional<Resultado> findByProyectoId(Long proyectoId);
    List<Resultado> findByEventoIdAndPublicado(Long eventoId, Boolean publicado);
}
