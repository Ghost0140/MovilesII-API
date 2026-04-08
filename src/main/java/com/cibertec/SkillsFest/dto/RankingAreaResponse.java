package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;

public record RankingAreaResponse(
        Long id,
        Long eventoId,
        String eventoNombre,
        Long usuarioId,
        String usuarioNombre,
        String area,
        BigDecimal score,
        Integer posicion
) {
}