package com.cibertec.SkillsFest.dto;

public record UsuarioResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String numeroDocumento,
        String carrera,
        Integer ciclo,
        String codigoEstudiante,
        String githubUsername,
        Boolean activo,
        Long sedeId,
        String sedeNombre
) {
}