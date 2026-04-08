package com.cibertec.SkillsFest.dto;

public record ProyectoResponse(
        Long id,
        String titulo,
        String resumen,
        String descripcion,
        String repositorioUrl,
        String videoUrl,
        String demoUrl,
        String tecnologias,
        String estado,
        Long eventoId,
        String eventoNombre,
        Long equipoId,
        String equipoNombre,
        Long usuarioId,
        String usuarioNombre
) {
}