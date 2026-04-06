package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Evaluacion;
import com.cibertec.SkillsFest.service.IEvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluacionController {

    private final IEvaluacionService evaluacionService;

    public record CrearEvaluacionRequest(
            Long proyectoId,
            Long juradoId,
            Long criterioId,
            BigDecimal puntaje,
            String comentario
    ) {}

    public record ActualizarEvaluacionRequest(
            BigDecimal puntaje,
            String comentario
    ) {}

    @GetMapping
    public ResponseEntity<?> obtenerTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Evaluacion> evaluaciones = evaluacionService.obtenerTodosPaginado(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Evaluaciones obtenidas exitosamente");
        response.put("data", evaluaciones.getContent());
        response.put("paginaActual", evaluaciones.getNumber());
        response.put("totalPaginas", evaluaciones.getTotalPages());
        response.put("totalElementos", evaluaciones.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Evaluacion> evaluacion = evaluacionService.obtenerPorId(id);

        if (evaluacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Evaluación no encontrada"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluación obtenida",
                "data", evaluacion.get()
        ));
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<?> obtenerPorProyecto(@PathVariable Long proyectoId) {
        List<Evaluacion> evaluaciones = evaluacionService.obtenerPorProyecto(proyectoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluaciones del proyecto obtenidas",
                "data", evaluaciones,
                "cantidad", evaluaciones.size()
        ));
    }

    @GetMapping("/jurado/{juradoId}")
    public ResponseEntity<?> obtenerPorJurado(@PathVariable Long juradoId) {
        List<Evaluacion> evaluaciones = evaluacionService.obtenerPorJurado(juradoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluaciones del jurado obtenidas",
                "data", evaluaciones,
                "cantidad", evaluaciones.size()
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearEvaluacionRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Evaluacion evaluacionGuardada = evaluacionService.crear(
                    request.proyectoId(),
                    request.juradoId(),
                    request.criterioId(),
                    request.puntaje(),
                    request.comentario()
            );

            response.put("mensaje", "Evaluación creada exitosamente");
            response.put("data", evaluacionGuardada);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("error", "Error al crear evaluación");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActualizarEvaluacionRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Evaluacion evaluacionActualizada = new Evaluacion();
            evaluacionActualizada.setPuntaje(request.puntaje());
            evaluacionActualizada.setComentario(request.comentario());

            Evaluacion evaluacion = evaluacionService.actualizar(id, evaluacionActualizada);

            response.put("mensaje", "Evaluación actualizada exitosamente");
            response.put("data", evaluacion);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", "Error al actualizar evaluación");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        evaluacionService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Evaluación eliminada exitosamente"));
    }
}