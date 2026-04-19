package com.cibertec.SkillsFest.dto.app;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppHistorialProyectoResponse {

    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String email;

    private Integer totalProyectos;
    private Integer totalIndividuales;
    private Integer totalGrupales;
    private Integer totalAprobados;
    private Integer totalEnviados;

    private List<AppProyectoResponse> activos;
    private List<AppProyectoResponse> historicos;
}