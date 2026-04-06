package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Evento;

import java.util.List;

public interface IEventoService {
    List<Evento> obtenerEventosPublicados();
    Evento crearEvento(Evento evento, Long creadorId, Long sedeId);
    Evento eliminarLogico(Long eventoId);
}