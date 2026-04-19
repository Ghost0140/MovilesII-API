package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppRankingAreaResponse;
import com.cibertec.SkillsFest.entity.RankingArea;
import com.cibertec.SkillsFest.repository.IRankingAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/rankings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppRankingController {

    private final IRankingAreaRepository rankingAreaRepository;

    @GetMapping("/areas")
    public ResponseEntity<?> listarAreasDisponibles() {
        return ResponseEntity.ok(Map.of(
                "areas", List.of(
                        "FRONTEND",
                        "BACKEND",
                        "BD",
                        "MOBILE",
                        "TESTING",
                        "FULLSTACK"
                )
        ));
    }

    @GetMapping("/evento/{eventoId}/{area}")
    public ResponseEntity<List<AppRankingAreaResponse>> obtenerRankingPorEventoYArea(
            @PathVariable Long eventoId,
            @PathVariable String area
    ) {
        String areaNormalizada = area.toUpperCase();

        List<AppRankingAreaResponse> rankings = rankingAreaRepository
                .findByEventoIdAndArea(eventoId, areaNormalizada)
                .stream()
                .sorted(Comparator.comparing(RankingArea::getPosicion))
                .map(this::mapRanking)
                .toList();

        return ResponseEntity.ok(rankings);
    }

    private AppRankingAreaResponse mapRanking(RankingArea ranking) {
        String sedeNombre = null;

        if (ranking.getUsuario() != null && ranking.getUsuario().getSede() != null) {
            sedeNombre = ranking.getUsuario().getSede().getNombre();
        }

        return AppRankingAreaResponse.builder()
                .id(ranking.getId())

                .eventoId(ranking.getEvento() != null ? ranking.getEvento().getId() : null)
                .eventoNombre(ranking.getEvento() != null ? ranking.getEvento().getNombre() : null)

                .usuarioId(ranking.getUsuario() != null ? ranking.getUsuario().getId() : null)
                .nombres(ranking.getUsuario() != null ? ranking.getUsuario().getNombres() : null)
                .apellidos(ranking.getUsuario() != null ? ranking.getUsuario().getApellidos() : null)
                .email(ranking.getUsuario() != null ? ranking.getUsuario().getEmail() : null)
                .githubUsername(ranking.getUsuario() != null ? ranking.getUsuario().getGithubUsername() : null)
                .sede(sedeNombre)

                .area(ranking.getArea())
                .score(ranking.getScore())
                .posicion(ranking.getPosicion())
                .build();
    }
}