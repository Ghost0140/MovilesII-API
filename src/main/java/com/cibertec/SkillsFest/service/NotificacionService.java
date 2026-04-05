package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Notificacion;
import java.util.List;
import java.util.Optional;

public interface NotificacionService {
    Notificacion enviar(Notificacion notificacion);
    Optional<Notificacion> obtenerPorId(Long id);
    List<Notificacion> listarPorUsuario(Long usuarioId);
    List<Notificacion> listarNoLeidasPorUsuario(Long usuarioId);
    void marcarComoLeida(Long id);
    void eliminar(Long id);
}