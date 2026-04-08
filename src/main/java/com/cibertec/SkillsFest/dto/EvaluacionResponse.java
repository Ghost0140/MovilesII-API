package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;
import java.util.Date;

public record EvaluacionResponse(
        Long id,
        Long proyectoId,
        String proyectoTitulo,
        Long juradoId,
        String juradoNombre,
        Long criterioId,
        String criterioNombre,
        BigDecimal puntaje,
        String comentario,
        Date evaluadoEn
) {
}