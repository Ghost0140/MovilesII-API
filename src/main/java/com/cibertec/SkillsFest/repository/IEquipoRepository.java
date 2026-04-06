package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByEventoIdAndEstadoNot(Long eventoId, String estado);
    List<Equipo> findByLiderIdAndEstadoNot(Long liderId, String estado);
    List<Equipo> findByEventoIdAndEstado(Long eventoId, String estado);
}