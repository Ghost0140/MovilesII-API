package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.time.LocalDateTime;

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

    private LocalDateTime creadoEn;
}