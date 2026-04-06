package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEventoIdAndEstadoNot(Long eventoId, String estado);
    Page<Proyecto> findByEventoIdAndEstadoNot(Long eventoId, String estado, Pageable pageable);
    List<Proyecto> findByEquipoIdAndEstadoNot(Long equipoId, String estado);
    List<Proyecto> findByUsuarioIdAndEstadoNot(Long usuarioId, String estado);
    List<Proyecto> findByEstado(String estado);
    Page<Proyecto> findByEstado(String estado, Pageable pageable);
    List<Proyecto> findByEventoIdAndEstado(Long eventoId, String estado);
}