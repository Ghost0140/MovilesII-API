package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProyectoService {
    List<Proyecto> obtenerTodos();
    Page<Proyecto> obtenerTodosPaginado(Pageable pageable);
    Optional<Proyecto> obtenerPorId(Long id);
    Proyecto crear(Proyecto proyecto, Long eventoId, Long equipoId, Long usuarioId);
    Proyecto actualizar(Long id, Proyecto proyecto);
    Proyecto cambiarEstado(Long id, String estado);
    void eliminar(Long id);
    List<Proyecto> obtenerPorEvento(Long eventoId);
    List<Proyecto> obtenerPorEstado(String estado);
    Proyecto enviarProyecto(Long proyectoId);
    Proyecto aprobarProyecto(Long proyectoId);
    Proyecto rechazarProyecto(Long proyectoId);
}