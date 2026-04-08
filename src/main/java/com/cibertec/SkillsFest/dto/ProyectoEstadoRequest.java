package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotBlank;

public record ProyectoEstadoRequest(
        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}