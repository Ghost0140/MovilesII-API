package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Repositorio;

import java.util.List;
import java.util.Optional;

public interface IRepositorioService {
    List<Repositorio> obtenerTodos();
    Optional<Repositorio> obtenerPorId(Long id);
    Optional<Repositorio> obtenerPorProyecto(Long proyectoId);
    void reanalizarPorProyecto(Long proyectoId);
}