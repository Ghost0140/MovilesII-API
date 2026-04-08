package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Equipo;

import java.util.List;
import java.util.Optional;

public interface IEquipoService {
	List<Equipo> obtenerTodos();
	Optional<Equipo> obtenerPorId(Long id);
	Equipo inscribirEquipo(Long eventoId, Long liderId, List<Long> miembrosIds, Long asesorId, String nombreEquipo);
	List<Equipo> obtenerEquiposPorEvento(Long eventoId);
	Equipo aprobarEquipo(Long equipoId, Long organizadorId);
	Equipo cambiarEstado(Long equipoId, String estado);
	Equipo eliminarLogico(Long equipoId);
}