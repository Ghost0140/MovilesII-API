package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiRadarResponse {

    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String email;
    private String githubUsername;

    private String estado;
    private String mensaje;

    private Long repositorioId;
    private String repositorioUrl;
    private Integer totalCommits;
    private Integer contributorsGithub;
    private Integer usuariosMapeados;
    private Integer contribucionesGeneradas;
    private LocalDateTime ultimoAnalisis;

    private BigDecimal radarFrontend;
    private BigDecimal radarBackend;
    private BigDecimal radarBd;
    private BigDecimal radarMobile;
    private BigDecimal radarTesting;

    private Integer commitsUsuario;
    private Integer lineasUsuario;

    private BigDecimal scoreFrontend;
    private BigDecimal scoreBackend;
    private BigDecimal scoreBd;
    private BigDecimal scoreMobile;
    private BigDecimal scoreTesting;

    private String tecnologiasDetectadas;
}