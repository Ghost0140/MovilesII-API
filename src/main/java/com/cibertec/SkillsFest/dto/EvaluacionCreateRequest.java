package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EvaluacionCreateRequest(

        @NotNull(message = "El proyecto es obligatorio")
        Long proyectoId,

        @NotNull(message = "El jurado es obligatorio")
        Long juradoId,

        @NotNull(message = "El criterio es obligatorio")
        Long criterioId,

        @NotNull(message = "El puntaje es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El puntaje no puede ser negativo")
        BigDecimal puntaje,

        String comentario
) {
}