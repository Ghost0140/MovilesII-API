package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventoCreateRequest(

        @NotNull(message = "La sede organizadora es obligatoria")
        Long sedeOrganizadoraId,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "El tipo es obligatorio")
        String tipo,

        @NotBlank(message = "El alcance es obligatorio")
        String alcance,

        @NotNull(message = "La fecha de inicio de inscripción es obligatoria")
        LocalDate fechaInicioInscripcion,

        @NotNull(message = "La fecha de fin de inscripción es obligatoria")
        LocalDate fechaFinInscripcion,

        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDate fechaEvento,

        Integer maxMiembrosEquipo,

        @NotNull(message = "El creador es obligatorio")
        Long creadoPorId
) {
}