package com.cibertec.SkillsFest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SkillsFest.entity.RankingArea;
import com.cibertec.SkillsFest.serviceImpl.TalentRadarServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller para TalentRadar
 * CRÍTICO: Endpoints para análisis de repositorios GitHub
 *
 * Funcionalidad:
 * 1. POST /analizar-proyecto/{id} → Obtiene datos de GitHub y calcula scores
 * 2. POST /generar-rankings/{eventoId} → Genera rankings por especialidad
 * 3. POST /actualizar-portfolio/{usuarioId} → Actualiza portafolio con datos radar
 * 4. GET /rankings/{eventoId}/{area} → Obtiene ranking de una área
 */
@Slf4j
@RestController
@RequestMapping("/api/radar")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TalentRadarController {

    private final TalentRadarServiceImpl talentRadarService;

    /**
     * ENDPOINT CRÍTICO: Analiza un proyecto y extrae datos de GitHub
     *
     * POST /api/radar/analizar-proyecto/123
     *
     * Pasos:
     * 1. Obtiene la URL del repositorio del proyecto
     * 2. Llama a GitHub API para obtener commits y lenguajes
     * 3. Analiza cada contribuidor
     * 4. Calcula scores por área (Frontend, Backend, BD, Mobile, Testing)
     * 5. Guarda en BD: Repositorio, Contribuciones, Scores
     */
    @PostMapping("/analizar-proyecto/{proyectoId}")
    public ResponseEntity<?> analizarProyecto(@PathVariable Long proyectoId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("========== INICIANDO ANÁLISIS DE TALENT RADAR ==========");
            log.info("Proyecto ID: {}", proyectoId);

            talentRadarService.analizarProyecto(proyectoId);

            response.put("mensaje", "Análisis de Talent Radar completado exitosamente");
            response.put("estado", "COMPLETADO");
            response.put("proyectoId", proyectoId);

            log.info("========== ANÁLISIS COMPLETADO ==========");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("ERROR en análisis de Talent Radar: {}", e.getMessage());

            response.put("error", "Error en análisis de Talent Radar");
            response.put("detalle", e.getMessage());
            response.put("estado", "ERROR");
            response.put("proyectoId", proyectoId);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera rankings por área de especialidad para un evento
     *
     * POST /api/radar/generar-rankings/1
     */
    @PostMapping("/generar-rankings/{eventoId}")
    public ResponseEntity<?> generarRankingsPorArea(@PathVariable Long eventoId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Generando rankings por área para evento: {}", eventoId);

            talentRadarService.generarRankingsPorArea(eventoId);

            response.put("mensaje", "Rankings generados exitosamente");
            response.put("eventoId", eventoId);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error generando rankings: {}", e.getMessage());

            response.put("error", "Error al generar rankings");
            response.put("detalle", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza el portafolio público con datos del radar
     *
     * POST /api/radar/actualizar-portfolio/123
     */
    @PostMapping("/actualizar-portfolio/{usuarioId}")
    public ResponseEntity<?> actualizarPortafolioRadar(@PathVariable Long usuarioId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Actualizando portafolio Radar para usuario: {}", usuarioId);

            talentRadarService.actualizarPortafolioRadar(usuarioId);

            response.put("mensaje", "Portafolio Radar actualizado exitosamente");
            response.put("usuarioId", usuarioId);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error actualizando portafolio: {}", e.getMessage());

            response.put("error", "Error al actualizar portafolio");
            response.put("detalle", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene el ranking de una área específica en un evento
     *
     * GET /api/radar/rankings/1/FRONTEND
     */
    @GetMapping("/rankings/{eventoId}/{area}")
    public ResponseEntity<?> obtenerRankingsPorArea(
            @PathVariable Long eventoId,
            @PathVariable String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Obteniendo rankings para área: {} en evento: {}", area, eventoId);

            List<RankingArea> rankings = talentRadarService.obtenerRankingsPorArea(eventoId, area);

            response.put("mensaje", "Rankings obtenidos exitosamente");
            response.put("area", area);
            response.put("eventoId", eventoId);
            response.put("data", rankings);
            response.put("cantidad", rankings.size());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error obteniendo rankings: {}", e.getMessage());

            response.put("error", "Error al obtener rankings");
            response.put("detalle", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ENDPOINT DE DEBUG: Estado de un análisis
     * Puede usarse para verificar si un análisis se completó
     *
     * GET /api/radar/status/proyecto/123
     */
    @GetMapping("/status/proyecto/{proyectoId}")
    public ResponseEntity<?> verificarEstadoAnalisis(@PathVariable Long proyectoId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // TODO: Implementar lógica para verificar estado
            // Por ahora, retorna respuesta genérica

            response.put("mensaje", "Estado del análisis");
            response.put("proyectoId", proyectoId);
            response.put("estado", "COMPLETADO");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", "Error verificando estado");
            response.put("detalle", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}