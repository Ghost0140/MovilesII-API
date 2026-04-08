package com.cibertec.SkillsFest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContribucionResponse(
        Long id,
        Long repositorioId,
        String repositorioUrl,
        Long usuarioId,
        String usuarioNombre,
        Integer totalCommits,
        Integer totalLineas,
        BigDecimal scoreFrontend,
        BigDecimal scoreBackend,
        BigDecimal scoreBd,
        BigDecimal scoreMobile,
        BigDecimal scoreTesting,
        String tecnologiasDetectadas,
        LocalDateTime analizadoEn
) {
}