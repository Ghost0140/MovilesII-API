package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Resultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IResultadoRepository extends JpaRepository<Resultado, Long> {
    List<Resultado> findByEventoIdAndEstadoNot(Long eventoId, String estado);
    Page<Resultado> findByEventoIdAndEstadoNot(Long eventoId, String estado, Pageable pageable);
    Optional<Resultado> findByProyectoId(Long proyectoId);
    List<Resultado> findByEventoIdAndEstado(Long eventoId, String estado);
}