package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppDashboardResponse {

    private UsuarioResumen usuario;
    private RadarResumen radar;
    private List<EventoResumen> eventosActivos;
    private List<ProyectoResumen> misProyectos;
    private ResumenGeneral resumen;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UsuarioResumen {
        private Long id;
        private String nombres;
        private String apellidos;
        private String email;
        private String roles;
        private String githubUsername;
        private String sede;
        private String carrera;
        private Integer ciclo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RadarResumen {
        private String estado;
        private String mensaje;

        private BigDecimal frontend;
        private BigDecimal backend;
        private BigDecimal bd;
        private BigDecimal mobile;
        private BigDecimal testing;

        private Integer totalCommits;
        private Integer contributorsGithub;
        private Integer usuariosMapeados;
        private Integer contribucionesGeneradas;
        private LocalDateTime ultimoAnalisis;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventoResumen {
        private Long id;
        private String nombre;
        private String tipo;
        private String alcance;
        private String estado;
        private Boolean permiteEquipos;
        private Integer maxMiembrosEquipo;
        private LocalDate fechaInicioInscripcion;
        private LocalDate fechaFinInscripcion;
        private LocalDate fechaEvento;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProyectoResumen {
        private Long id;
        private String titulo;
        private String resumen;
        private String estado;
        private String repositorioUrl;
        private String tipoParticipacion;
        private Long eventoId;
        private String eventoNombre;
        private LocalDateTime fechaEnvio;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResumenGeneral {
        private Integer totalEventosActivos;
        private Integer totalMisProyectos;
        private Integer totalProyectosEnviados;
        private Integer totalProyectosAprobados;
    }
}