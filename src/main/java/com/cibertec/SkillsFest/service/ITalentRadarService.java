package com.cibertec.SkillsFest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import com.cibertec.SkillsFest.entity.Resultado;
import com.cibertec.SkillsFest.entity.RankingArea;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.entity.Notificacion;
import com.cibertec.SkillsFest.entity.Comentario;
import com.cibertec.SkillsFest.entity.RankingSede;



public interface ITalentRadarService {
    void analizarProyecto(Long proyectoId); // Extrae datos del repositorio GitHub
    void generarRankingsPorArea(Long eventoId); // Calcula rankings de especialidades
    void actualizarPortafolioRadar(Long usuarioId); // Actualiza datos del portafolio
    List<RankingArea> obtenerRankingsPorArea(Long eventoId, String area);
}