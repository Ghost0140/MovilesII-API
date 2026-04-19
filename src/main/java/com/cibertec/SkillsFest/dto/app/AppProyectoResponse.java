package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppProyectoResponse {

    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private String estado;

    private String repositorioUrl;
    private String videoUrl;
    private String demoUrl;
    private String tecnologias;

    private String tipoParticipacion;

    private Long eventoId;
    private String eventoNombre;
    private String eventoEstado;
    private Boolean permiteEquipos;

    private Long equipoId;
    private String equipoNombre;
    private String equipoEstado;

    private LocalDateTime fechaEnvio;

    private String estadoRadar;
    private Integer totalCommits;
    private Integer contributorsGithub;
    private Integer usuariosMapeados;
    private Integer contribucionesGeneradas;
}