package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Contribucion;

import java.util.List;
import java.util.Optional;

public interface ContribucionService {
    Contribucion guardar(Contribucion contribucion);
    Optional<Contribucion> obtenerPorId(Long id);
    List<Contribucion> listarTodos();
    List<Contribucion> listarPorRepositorio(Long repositorioId);
    List<Contribucion> listarPorUsuario(Long usuarioId);
    Optional<Contribucion> obtenerPorRepositorioYUsuario(Long repositorioId, Long usuarioId);
    void eliminar(Long id);
}