package com.cibertec.SkillsFest.dto.app;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEquipoRequest {

    private Long eventoId;
    private String nombre;
}