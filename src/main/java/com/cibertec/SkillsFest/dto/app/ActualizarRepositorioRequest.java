package com.cibertec.SkillsFest.dto.app;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActualizarRepositorioRequest {

    private String repositorioUrl;
}