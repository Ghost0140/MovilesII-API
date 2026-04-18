package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.radar.RadarAnalysisResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.entity.RankingArea;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.repository.IRepositorioRepository;
import com.cibertec.SkillsFest.serviceImpl.TalentRadarServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/radar")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TalentRadarController {

    private final TalentRadarServiceImpl talentRadarService;
    private final IRepositorioRepository repositorioRepository;
    private final IContribucionRepository contribucionRepository;

    /**
     * Analiza un proyecto con Talent Radar.
     *
     * POST /api/radar/analizar-proyecto/{proyectoId}
     */
    @PostMapping("/analizar-proyecto/{proyectoId}")
    public ResponseEntity<?> analizarProyecto(@PathVariable Long proyectoId) {
        try {
            log.info("========== INICIANDO ANÁLISIS DE TALENT RADAR ==========");
            log.info("Proyecto ID: {}", proyectoId);

            RadarAnalysisResponse response = talentRadarService.analizarProyecto(proyectoId);

            log.info("========== ANÁLISIS FINALIZADO ==========");
            log.info("Estado: {}", response.getEstado());

            if ("COMPLETADO".equalsIgnoreCase(response.getEstado())) {
                return ResponseEntity.ok(response);
            }

            if ("INCOMPLETO".equalsIgnoreCase(response.getEstado())) {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.error("ERROR en análisis de Talent Radar: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error en análisis de Talent Radar");
            response.put("detalle", e.getMessage());
            response.put("estado", "ERROR");
            response.put("proyectoId", proyectoId);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Genera rankings por área para un evento.
     *
     * POST /api/radar/generar-rankings/{eventoId}
     */
    @PostMapping("/generar-rankings/{eventoId}")
    public ResponseEntity<?> generarRankingsPorArea(@PathVariable Long eventoId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Generando rankings por área para evento: {}", eventoId);

            talentRadarService.generarRankingsPorArea(eventoId);

            response.put("mensaje", "Rankings generados exitosamente");
            response.put("eventoId", eventoId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generando rankings: {}", e.getMessage());

            response.put("error", "Error al generar rankings");
            response.put("detalle", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Actualiza el portafolio público con datos del Radar.
     *
     * POST /api/radar/actualizar-portfolio/{usuarioId}
     */
    @PostMapping("/actualizar-portfolio/{usuarioId}")
    public ResponseEntity<?> actualizarPortafolioRadar(@PathVariable Long usuarioId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Actualizando portafolio Radar para usuario: {}", usuarioId);

            talentRadarService.actualizarPortafolioRadar(usuarioId);

            response.put("mensaje", "Portafolio Radar actualizado exitosamente");
            response.put("usuarioId", usuarioId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error actualizando portafolio: {}", e.getMessage());

            response.put("error", "Error al actualizar portafolio");
            response.put("detalle", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene ranking de una área específica.
     *
     * GET /api/radar/rankings/{eventoId}/{area}
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
            response.put("area", area.toUpperCase());
            response.put("eventoId", eventoId);
            response.put("data", rankings);
            response.put("cantidad", rankings.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo rankings: {}", e.getMessage());

            response.put("error", "Error al obtener rankings");
            response.put("detalle", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Estado real del análisis de un proyecto.
     *
     * GET /api/radar/status/proyecto/{proyectoId}
     */
    @GetMapping("/status/proyecto/{proyectoId}")
    public ResponseEntity<?> verificarEstadoAnalisis(@PathVariable Long proyectoId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Repositorio repositorio = repositorioRepository.findByProyectoId(proyectoId)
                    .orElse(null);

            if (repositorio == null) {
                response.put("proyectoId", proyectoId);
                response.put("estado", "PENDIENTE");
                response.put("mensaje", "El proyecto todavía no tiene análisis de Radar");
                response.put("repositorioId", null);
                response.put("totalCommits", 0);
                response.put("contributorsGithub", 0);
                response.put("usuariosMapeados", 0);
                response.put("contribucionesGeneradas", 0);
                response.put("detalleError", null);

                return ResponseEntity.ok(response);
            }

            List<Contribucion> contribuciones = contribucionRepository.findByRepositorioId(repositorio.getId());

            response.put("proyectoId", proyectoId);
            response.put("repositorioId", repositorio.getId());
            response.put("repositorioUrl", repositorio.getUrl());
            response.put("estado", repositorio.getEstadoAnalisis());
            response.put("detalleError", repositorio.getDetalleError());
            response.put("ultimoAnalisis", repositorio.getUltimoAnalisis());
            response.put("totalCommits", repositorio.getTotalCommits());
            response.put("contributorsGithub", repositorio.getContributorsGithub());
            response.put("usuariosMapeados", repositorio.getUsuariosMapeados());
            response.put("contribucionesGeneradas", repositorio.getContribucionesGeneradas());
            response.put("contribucionesRegistradas", contribuciones.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error verificando estado");
            response.put("detalle", e.getMessage());
            response.put("estado", "ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}