package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppRankingStatusResponse;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.RankingArea;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IRankingAreaRepository;
import com.cibertec.SkillsFest.service.ITalentRadarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/admin/rankings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppAdminRankingController {

    private final ITalentRadarService talentRadarService;
    private final IEventoRepository eventoRepository;
    private final IRankingAreaRepository rankingAreaRepository;

    @PostMapping("/generar/{eventoId}")
    public ResponseEntity<?> generarRankings(
            @PathVariable Long eventoId,
            Authentication authentication
    ) {
        validarRolAdminProfesor(authentication);

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if ("ELIMINADO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("No se puede generar ranking de un evento eliminado");
        }

        talentRadarService.generarRankingsPorArea(eventoId);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Rankings generados correctamente");
        response.put("eventoId", eventoId);
        response.put("eventoNombre", evento.getNombre());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{eventoId}")
    public ResponseEntity<AppRankingStatusResponse> obtenerStatusRanking(
            @PathVariable Long eventoId,
            Authentication authentication
    ) {
        validarRolAdminProfesor(authentication);

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        List<RankingArea> rankings = rankingAreaRepository.findByEventoId(eventoId);

        int frontend = contarPorArea(rankings, "FRONTEND");
        int backend = contarPorArea(rankings, "BACKEND");
        int bd = contarPorArea(rankings, "BD");
        int mobile = contarPorArea(rankings, "MOBILE");
        int testing = contarPorArea(rankings, "TESTING");

        String estado = rankings.isEmpty() ? "PENDIENTE" : "GENERADO";
        String mensaje = rankings.isEmpty()
                ? "Todavía no se han generado rankings para este evento"
                : "Rankings generados correctamente";

        AppRankingStatusResponse response = AppRankingStatusResponse.builder()
                .eventoId(evento.getId())
                .eventoNombre(evento.getNombre())
                .totalRankings(rankings.size())
                .rankingsFrontend(frontend)
                .rankingsBackend(backend)
                .rankingsBd(bd)
                .rankingsMobile(mobile)
                .rankingsTesting(testing)
                .estado(estado)
                .mensaje(mensaje)
                .build();

        return ResponseEntity.ok(response);
    }

    private int contarPorArea(List<RankingArea> rankings, String area) {
        return (int) rankings.stream()
                .filter(r -> area.equalsIgnoreCase(r.getArea()))
                .count();
    }

    private void validarRolAdminProfesor(Authentication authentication) {
        boolean autorizado = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN") ||
                                a.getAuthority().equals("ROLE_ORGANIZADOR") ||
                                a.getAuthority().equals("ROLE_PROFESOR")
                );

        if (!autorizado) {
            throw new RuntimeException("No tienes permisos para administrar rankings");
        }
    }
}