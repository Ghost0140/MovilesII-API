package com.cibertec.SkillsFest.dto.radar;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RadarAnalysisResponse {

    private Long proyectoId;
    private Long repositorioId;

    private String repositorioUrl;
    private String estado;

    private Integer totalCommits;
    private Integer contributorsGithub;
    private Integer usuariosMapeados;
    private Integer contribucionesGeneradas;

    private Map<String, Double> lenguajesDetectados;

    @Builder.Default
    private List<String> advertencias = new ArrayList<>();

    private String mensaje;
}