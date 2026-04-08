package com.cibertec.SkillsFest.dto;

public record EquipoResponse(
        Long id,
        String nombre,
        String estado,
        Long eventoId,
        String eventoNombre,
        Long sedeId,
        String sedeNombre,
        Long liderId,
        String liderNombre,
        Long asesorId,
        String asesorNombre,
        String miembros
) {
}