package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;

public record PortafolioResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        Boolean visible,
        Boolean activo,
        String slug,
        String titulo,
        String bio,
        Integer totalEventos,
        Integer totalProyectos,
        Integer premiosObtenidos,
        BigDecimal radarFrontend,
        BigDecimal radarBackend,
        BigDecimal radarBd,
        BigDecimal radarMobile,
        BigDecimal radarTesting
) {
}