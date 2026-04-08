package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;

public record ResultadoResponse(
        Long id,
        Long eventoId,
        String eventoNombre,
        Long proyectoId,
        String proyectoTitulo,
        BigDecimal puntajeJurados,
        BigDecimal puntajePopular,
        BigDecimal puntajeTotal,
        Integer posicion,
        String categoriaPremio,
        String estado
) {
}