package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppContribuidorResponse {

    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String email;
    private String githubUsername;
    private String sede;
    private String carrera;
    private Integer ciclo;

    private Long contribucionId;
    private Long repositorioId;
    private String repositorioUrl;

    private Long proyectoId;
    private String proyectoTitulo;
    private String proyectoResumen;
    private String tipoParticipacion;

    private Long eventoId;
    private String eventoNombre;

    private Integer totalCommits;
    private Integer totalLineas;

    private BigDecimal scoreFrontend;
    private BigDecimal scoreBackend;
    private BigDecimal scoreBd;
    private BigDecimal scoreMobile;
    private BigDecimal scoreTesting;
    private BigDecimal scorePromedio;

    private String tecnologiasDetectadas;
    private LocalDateTime analizadoEn;
}