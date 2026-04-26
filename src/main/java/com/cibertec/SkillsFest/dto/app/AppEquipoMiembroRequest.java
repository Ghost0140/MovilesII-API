package com.cibertec.SkillsFest.dto.app;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppEquipoMiembroRequest {

    private Long usuarioId;
    private String email;
    private String codigoEstudiante;
}
