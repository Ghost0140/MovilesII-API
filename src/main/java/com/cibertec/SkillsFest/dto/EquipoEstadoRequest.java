package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipoEstadoRequest(
        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}