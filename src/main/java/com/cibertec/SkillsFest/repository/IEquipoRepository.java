package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Equipo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IEquipoRepository extends JpaRepository<Equipo,Long> {
	// Listar todos los equipos inscritos en un evento específico
    List<Equipo> findByEventoId(Long eventoId);

    // Para que un alumno vea los equipos que él ha creado/lidera
    List<Equipo> findByLiderId(Long liderId);

    // Listar solo los equipos que ya fueron aprobados por el organizador en un evento
    List<Equipo> findByEventoIdAndAprobadoTrue(Long eventoId);
}
