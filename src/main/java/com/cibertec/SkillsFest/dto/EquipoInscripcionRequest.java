package com.cibertec.SkillsFest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EquipoInscripcionRequest(

        @NotNull(message = "El evento es obligatorio")
        Long eventoId,

        @NotNull(message = "El líder es obligatorio")
        Long liderId,

        List<Long> miembrosIds,

        Long asesorId,

        @NotBlank(message = "El nombre del equipo es obligatorio")
        String nombreEquipo
) {
}