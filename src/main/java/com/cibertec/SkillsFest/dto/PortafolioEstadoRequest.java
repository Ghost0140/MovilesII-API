package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotNull;

public record PortafolioEstadoRequest(
        @NotNull(message = "El campo activo es obligatorio")
        Boolean activo,

        @NotNull(message = "El campo visible es obligatorio")
        Boolean visible
) {
}