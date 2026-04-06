package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Resultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IResultadoService {
    List<Resultado> obtenerTodos();
    Page<Resultado> obtenerTodosPaginado(Pageable pageable);
    Optional<Resultado> obtenerPorId(Long id);
    Resultado crear(Resultado resultado);
    Resultado actualizar(Long id, Resultado resultado);
    void eliminar(Long id);
    List<Resultado> obtenerPorEvento(Long eventoId);
    Resultado calcularResultados(Long eventoId, Long proyectoId);
    void publicarResultados(Long eventoId);
}