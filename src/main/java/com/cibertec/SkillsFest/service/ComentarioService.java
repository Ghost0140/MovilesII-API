package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Comentario;
import java.util.List;
import java.util.Optional;

public interface ComentarioService {
    Comentario guardar(Comentario comentario);
    Optional<Comentario> obtenerPorId(Long id);
    List<Comentario> listarTodos();
    List<Comentario> listarPorProyecto(Long proyectoId);
    void eliminarLogico(Long id);
}