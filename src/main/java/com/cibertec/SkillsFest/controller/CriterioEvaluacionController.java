package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import com.cibertec.SkillsFest.service.ICriterioEvaluacionService;
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
@RequestMapping("/api/criterios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CriterioEvaluacionController {

    private final ICriterioEvaluacionService criterioService;

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CriterioEvaluacion> criterios = criterioService.obtenerTodosPaginado(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Criterios obtenidos exitosamente");
        response.put("data", criterios.getContent());
        response.put("paginaActual", criterios.getNumber());
        response.put("totalPaginas", criterios.getTotalPages());
        response.put("totalElementos", criterios.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<CriterioEvaluacion> criterio = criterioService.obtenerPorId(id);

        if (criterio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Criterio no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Criterio obtenido",
                "data", criterio.get()
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(Map.of(
                "mensaje", "Criterios del evento obtenidos",
                "data", criterioService.obtenerPorEvento(eventoId)
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CriterioEvaluacion criterio) {
        CriterioEvaluacion creado = criterioService.crear(criterio);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Criterio creado exitosamente",
                "data", creado
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CriterioEvaluacion criterio) {
        CriterioEvaluacion actualizado = criterioService.actualizar(id, criterio);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Criterio actualizado exitosamente",
                "data", actualizado
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        criterioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Criterio eliminado exitosamente"));
    }
}