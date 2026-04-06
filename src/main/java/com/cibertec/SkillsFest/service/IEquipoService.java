package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Equipo;

import java.util.List;

public interface IEquipoService {
	Equipo inscribirEquipo(Long eventoId, Long liderId, List<Long> miembrosIds, Long asesorId, String nombreEquipo);
	List<Equipo> obtenerEquiposPorEvento(Long eventoId);
	Equipo aprobarEquipo(Long equipoId, Long organizadorId);
	Equipo eliminarLogico(Long equipoId);
}