package com.cibertec.SkillsFest.service;

import java.util.List;

import com.cibertec.SkillsFest.entity.Evento;

public interface IEventoService {

	// Para la pantalla del alumno (Cartelera)
    List<Evento> obtenerEventosPublicados();
    
    // Para la pantalla del Profesor/Organizador
    Evento crearEvento(Evento evento, Long creadorId, Long sedeId);
}
