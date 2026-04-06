package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.RankingSede;
import com.cibertec.SkillsFest.service.IRankingSedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Ranking de sedes obtenido exitosamente");
        response.put("data", rankings.getContent());
        response.put("paginaActual", rankings.getNumber());
        response.put("totalPaginas", rankings.getTotalPages());
        response.put("totalElementos", rankings.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<RankingSede> ranking = rankingSedeService.obtenerPorId(id);

        if (ranking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Ranking de sede no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sede obtenido",
                "data", ranking.get()
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sedes del evento obtenido",
                "data", rankingSedeService.obtenerPorEvento(eventoId)
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RankingSede ranking) {
        RankingSede creado = rankingSedeService.crear(ranking);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Ranking de sede creado exitosamente",
                "data", creado
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody RankingSede ranking) {
        RankingSede actualizado = rankingSedeService.actualizar(id, ranking);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ranking de sede actualizado exitosamente",
                "data", actualizado
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        rankingSedeService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Ranking de sede eliminado exitosamente"));
    }
}