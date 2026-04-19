package com.cibertec.SkillsFest.dto.app;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppProyectoRequest {

    private Long eventoId;

    private String titulo;
    private String resumen;
    private String descripcion;

    private String repositorioUrl;
    private String videoUrl;
    private String demoUrl;

    private String tecnologias;
}