package com.cibertec.SkillsFest.dto.app;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppRankingStatusResponse {

    private Long eventoId;
    private String eventoNombre;

    private Integer totalRankings;
    private Integer rankingsFrontend;
    private Integer rankingsBackend;
    private Integer rankingsBd;
    private Integer rankingsMobile;
    private Integer rankingsTesting;

    private String estado;
    private String mensaje;
}