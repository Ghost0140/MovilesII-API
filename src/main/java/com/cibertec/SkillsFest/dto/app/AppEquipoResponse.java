package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEquipoResponse {

    private Long id;
    private String nombre;
    private String estado;

    private Long eventoId;
    private String eventoNombre;

    private Long sedeId;
    private String sedeNombre;

    private Long liderId;
    private String liderNombre;

    private String miembros;
    private List<Long> miembrosIds;
    private List<String> miembrosNombres;
    private Integer cantidadMiembros;
    private Integer maxMiembrosEquipo;
    private Boolean esLider;
    private Boolean congelado;
    private Boolean puedeEditar;

    private LocalDateTime creadoEn;
}
