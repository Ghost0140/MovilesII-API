package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppPortafolioResponse {

    private Long portafolioId;

    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String email;
    private String githubUsername;
    private String sede;
    private String carrera;
    private Integer ciclo;

    private Boolean visible;
    private Boolean activo;
    private String slug;
    private String titulo;
    private String bio;

    private Integer totalEventos;
    private Integer totalProyectos;
    private Integer premiosObtenidos;

    private BigDecimal radarFrontend;
    private BigDecimal radarBackend;
    private BigDecimal radarBd;
    private BigDecimal radarMobile;
    private BigDecimal radarTesting;

    private LocalDateTime actualizadoEn;
}