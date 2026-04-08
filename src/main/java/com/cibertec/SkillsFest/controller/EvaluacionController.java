package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.EvaluacionCreateRequest;
import com.cibertec.SkillsFest.dto.EvaluacionResponse;
import com.cibertec.SkillsFest.entity.Evaluacion;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IEvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluacionController {

    private final IEvaluacionService evaluacionService;

    @GetMapping
    public ResponseEntity<?> obtenerTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Evaluacion> evaluaciones = evaluacionService.obtenerTodosPaginado(pageable);

        List<EvaluacionResponse> data = evaluaciones.getContent()
                .stream()
                .map(ApiMapper::toEvaluacionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluaciones obtenidas exitosamente",
                "data", data,
                "paginaActual", evaluaciones.getNumber(),
                "totalPaginas", evaluaciones.getTotalPages(),
                "totalElementos", evaluaciones.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Evaluacion evaluacion = evaluacionService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluación obtenida",
                "data", ApiMapper.toEvaluacionResponse(evaluacion)
        ));
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<?> obtenerPorProyecto(@PathVariable Long proyectoId) {
        List<EvaluacionResponse> data = evaluacionService.obtenerPorProyecto(proyectoId)
                .stream()
                .map(ApiMapper::toEvaluacionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluaciones del proyecto obtenidas",
                "data", data,
                "cantidad", data.size()
        ));
    }

    @GetMapping("/jurado/{juradoId}")
    public ResponseEntity<?> obtenerPorJurado(@PathVariable Long juradoId) {
        List<EvaluacionResponse> data = evaluacionService.obtenerPorJurado(juradoId)
                .stream()
                .map(ApiMapper::toEvaluacionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluaciones del jurado obtenidas",
                "data", data,
                "cantidad", data.size()
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody EvaluacionCreateRequest request) {
        Evaluacion evaluacionGuardada = evaluacionService.crear(
                request.proyectoId(),
                request.juradoId(),
                request.criterioId(),
                request.puntaje(),
                request.comentario()
        );

        return new ResponseEntity<>(Map.of(
                "mensaje", "Evaluación creada exitosamente",
                "data", ApiMapper.toEvaluacionResponse(evaluacionGuardada)
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        evaluacionService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Evaluación eliminada exitosamente"));
    }
}