package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.dto.radar.RadarAnalysisResponse;
import com.cibertec.SkillsFest.entity.RankingArea;

import java.util.List;

public interface ITalentRadarService {

    RadarAnalysisResponse analizarProyecto(Long proyectoId);

    void generarRankingsPorArea(Long eventoId);

    void actualizarPortafolioRadar(Long usuarioId);

    List<RankingArea> obtenerRankingsPorArea(Long eventoId, String area);
}