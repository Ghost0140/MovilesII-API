package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICriterioEvaluacionService {
    List<CriterioEvaluacion> obtenerTodos();
    Page<CriterioEvaluacion> obtenerTodosPaginado(Pageable pageable);
    Optional<CriterioEvaluacion> obtenerPorId(Long id);
    CriterioEvaluacion crear(CriterioEvaluacion criterio);
    CriterioEvaluacion actualizar(Long id, CriterioEvaluacion criterio);
    void eliminar(Long id);
    List<CriterioEvaluacion> obtenerPorEvento(Long eventoId);
}