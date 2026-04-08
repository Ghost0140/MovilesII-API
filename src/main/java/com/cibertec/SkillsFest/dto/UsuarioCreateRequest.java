package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene formato válido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        String password,

        @Size(max = 50, message = "El número de documento no puede superar 50 caracteres")
        String numeroDocumento,

        @NotNull(message = "La sede es obligatoria")
        Long sedeId,

        @Size(max = 150, message = "La carrera no puede superar 150 caracteres")
        String carrera,

        @Min(value = 1, message = "El ciclo mínimo es 1")
        @Max(value = 12, message = "El ciclo máximo es 12")
        Integer ciclo,

        @Size(max = 20, message = "El código de estudiante no puede superar 20 caracteres")
        String codigoEstudiante,

        @Size(max = 100, message = "El githubUsername no puede superar 100 caracteres")
        String githubUsername
) {
}