package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.PortafolioPublico;

import java.util.List;
import java.util.Optional;

public interface IPortafolioPublicoService {
    List<PortafolioPublico> obtenerTodos();
    Optional<PortafolioPublico> obtenerPorId(Long id);
    Optional<PortafolioPublico> obtenerPorUsuario(Long usuarioId);
    Optional<PortafolioPublico> obtenerPorSlug(String slug);
    PortafolioPublico crear(PortafolioPublico portfolio);
    PortafolioPublico actualizar(Long id, PortafolioPublico portfolio);
    PortafolioPublico cambiarEstado(Long id, Boolean activo, Boolean visible);
    void eliminar(Long id);
}