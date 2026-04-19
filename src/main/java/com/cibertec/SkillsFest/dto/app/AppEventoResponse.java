package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEventoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private String alcance;
    private String estado;

    private Boolean permiteEquipos;
    private Integer maxMiembrosEquipo;
    private Boolean permiteVotacionPopular;

    private LocalDate fechaInicioInscripcion;
    private LocalDate fechaFinInscripcion;
    private LocalDate fechaEvento;

    private String sedeOrganizadora;
    private String bannerUrl;

    private Boolean inscripcionAbierta;
    private Boolean eventoActivo;
    private Boolean eventoFinalizado;
}