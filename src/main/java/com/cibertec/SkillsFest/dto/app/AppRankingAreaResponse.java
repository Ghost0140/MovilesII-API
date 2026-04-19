package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppRankingAreaResponse {

    private Long id;

    private Long eventoId;
    private String eventoNombre;

    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String email;
    private String githubUsername;
    private String sede;

    private String area;
    private BigDecimal score;
    private Integer posicion;
}