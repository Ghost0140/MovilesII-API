package com.cibertec.SkillsFest.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        Long totalUsuariosActivos,
        Long totalEventos,
        Long totalProyectos,
        List<Map<String, Object>> topUsuariosRadar,
        List<Map<String, Object>> topSedes,
        List<Map<String, Object>> ultimosEventos
) {
}