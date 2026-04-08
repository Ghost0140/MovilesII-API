package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Evento;

import java.util.List;
import java.util.Optional;

public interface IEventoService {
    List<Evento> obtenerTodos();
    Optional<Evento> obtenerPorId(Long id);
    List<Evento> obtenerEventosPublicados();
    Evento crearEvento(Evento evento, Long creadorId, Long sedeId);
    Evento actualizarEvento(Long id, Evento eventoActualizado);
    Evento cambiarEstado(Long id, String estado);
    Evento eliminarLogico(Long eventoId);
}