package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Evaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IEvaluacionService {
    List<Evaluacion> obtenerTodos();
    Page<Evaluacion> obtenerTodosPaginado(Pageable pageable);
    Optional<Evaluacion> obtenerPorId(Long id);
    Evaluacion crear(Long proyectoId, Long juradoId, Long criterioId, BigDecimal puntaje, String comentario);
    Evaluacion actualizar(Long id, Evaluacion evaluacion);
    void eliminar(Long id);
    List<Evaluacion> obtenerPorProyecto(Long proyectoId);
    List<Evaluacion> obtenerPorJurado(Long juradoId);
}