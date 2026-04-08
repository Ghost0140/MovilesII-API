package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;

public record RankingSedeResponse(
        Long id,
        Long eventoId,
        String eventoNombre,
        Long sedeId,
        String sedeNombre,
        Integer posicion,
        BigDecimal puntosTotales,
        Integer proyectosPresentados
) {
}