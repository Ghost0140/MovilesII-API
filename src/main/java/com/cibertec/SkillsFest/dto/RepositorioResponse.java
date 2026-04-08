package com.cibertec.SkillsFest.dto;

import java.time.LocalDateTime;

public record RepositorioResponse(
        Long id,
        Long proyectoId,
        String proyectoTitulo,
        String url,
        String plataforma,
        Integer totalCommits,
        String lenguajes,
        LocalDateTime ultimoAnalisis
) {
}