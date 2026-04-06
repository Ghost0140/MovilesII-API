package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEventoId(Long eventoId);
    Page<Proyecto> findByEventoId(Long eventoId, Pageable pageable);
    List<Proyecto> findByEquipoId(Long equipoId);
    List<Proyecto> findByUsuarioId(Long usuarioId);
    List<Proyecto> findByEstado(String estado);
    Page<Proyecto> findByEstado(String estado, Pageable pageable);
    List<Proyecto> findByEventoIdAndEstado(Long eventoId, String estado);
}
