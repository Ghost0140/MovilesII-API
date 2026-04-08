package com.cibertec.SkillsFest.dto;

import java.time.LocalDate;

public record EventoResponse(
        Long id,
        String nombre,
        String descripcion,
        String tipo,
        String alcance,
        LocalDate fechaInicioInscripcion,
        LocalDate fechaFinInscripcion,
        LocalDate fechaEvento,
        Boolean permiteEquipos,
        Integer maxMiembrosEquipo,
        Boolean permiteVotacionPopular,
        String estado,
        Long sedeOrganizadoraId,
        String sedeOrganizadoraNombre,
        Long creadoPorId,
        String creadoPorNombre
) {
}