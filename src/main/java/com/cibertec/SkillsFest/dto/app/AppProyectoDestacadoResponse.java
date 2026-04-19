package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppProyectoDestacadoResponse {

    private Long proyectoId;
    private String titulo;
    private String resumen;
    private String descripcion;
    private String estado;
    private String repositorioUrl;
    private String tipoParticipacion;

    private Long eventoId;
    private String eventoNombre;

    private Long equipoId;
    private String equipoNombre;

    private Integer totalCommits;
    private Integer contributorsGithub;
    private Integer usuariosMapeados;
    private Integer contribucionesGeneradas;
    private String estadoRadar;

    private BigDecimal scorePromedioProyecto;
}