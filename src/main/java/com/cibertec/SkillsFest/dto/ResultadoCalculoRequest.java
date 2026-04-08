package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotNull;

public record ResultadoCalculoRequest(
        @NotNull(message = "El evento es obligatorio")
        Long eventoId,

        @NotNull(message = "El proyecto es obligatorio")
        Long proyectoId
) {
}