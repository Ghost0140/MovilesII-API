package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.RankingSedeResponse;
import com.cibertec.SkillsFest.entity.RankingSede;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IRankingSedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking-sedes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RankingSedeController {

    private final IRankingSedeService rankingSedeService;

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RankingSede> rankings = rankingSedeService.obtenerTodosPaginado(pageable);

        List<RankingSedeResponse> data = rankings.getContent()
                .stream()
                .map(ApiMapper::toRankingSedeResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sedes obtenido",
                "data", data,
                "paginaActual", rankings.getNumber(),
                "totalPaginas", rankings.getTotalPages(),
                "totalElementos", rankings.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        RankingSede ranking = rankingSedeService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking de sede no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sede obtenido",
                "data", ApiMapper.toRankingSedeResponse(ranking)
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        List<RankingSedeResponse> data = rankingSedeService.obtenerPorEvento(eventoId)
                .stream()
                .map(ApiMapper::toRankingSedeResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sedes por evento obtenido",
                "data", data
        ));
    }
}