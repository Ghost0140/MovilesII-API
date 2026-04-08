package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioEstadoRequest(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) {
}