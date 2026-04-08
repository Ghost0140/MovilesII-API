package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(

        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        String nombres,

        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @NotNull(message = "La sede es obligatoria")
        Long sedeId,

        @Size(max = 150, message = "La carrera no puede superar 150 caracteres")
        String carrera,

        @Min(value = 1, message = "El ciclo mínimo es 1")
        @Max(value = 12, message = "El ciclo máximo es 12")
        Integer ciclo,

        @Size(max = 100, message = "El githubUsername no puede superar 100 caracteres")
        String githubUsername
) {
}