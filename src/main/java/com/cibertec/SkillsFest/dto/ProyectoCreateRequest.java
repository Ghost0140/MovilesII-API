package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProyectoCreateRequest(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El resumen es obligatorio")
        String resumen,

        String descripcion,
        String repositorioUrl,
        String videoUrl,
        String demoUrl,
        List<String> tecnologias,

        @NotNull(message = "El evento es obligatorio")
        Long eventoId,

        Long equipoId,
        Long usuarioId
) {
}