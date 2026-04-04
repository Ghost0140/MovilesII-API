package com.cibertec.SkillsFest.service;

import java.util.List;

import com.cibertec.SkillsFest.entity.Equipo;

public interface IEquipoService {

	Equipo inscribirEquipo(Long eventoId, Long liderId, List<Long> miembrosIds, Long asesorId, String nombreEquipo);
	
	List<Equipo> obtenerEquiposPorEvento(Long eventoId);
    
    Equipo aprobarEquipo(Long equipoId, Long organizadorId);
}

