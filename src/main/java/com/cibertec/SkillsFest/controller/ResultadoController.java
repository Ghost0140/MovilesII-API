package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Resultado;
import com.cibertec.SkillsFest.service.IResultadoService;
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
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResultadoController {

    private final IResultadoService resultadoService;

    public record CalcularResultadoRequest(Long eventoId, Long proyectoId) {}

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Resultado> resultados = resultadoService.obtenerTodosPaginado(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Resultados obtenidos exitosamente");
        response.put("data", resultados.getContent());
        response.put("paginaActual", resultados.getNumber());
        response.put("totalPaginas", resultados.getTotalPages());
        response.put("totalElementos", resultados.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Resultado> resultado = resultadoService.obtenerPorId(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Resultado no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultado obtenido",
                "data", resultado.get()
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultados del evento obtenidos",
                "data", resultadoService.obtenerPorEvento(eventoId)
        ));
    }

    @PostMapping("/calcular")
    public ResponseEntity<?> calcular(@RequestBody CalcularResultadoRequest request) {
        Resultado resultado = resultadoService.calcularResultados(request.eventoId(), request.proyectoId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultado calculado exitosamente",
                "data", resultado
        ));
    }

    @PostMapping("/publicar/{eventoId}")
    public ResponseEntity<?> publicar(@PathVariable Long eventoId) {
        resultadoService.publicarResultados(eventoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultados publicados exitosamente",
                "eventoId", eventoId
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        resultadoService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Resultado eliminado lógicamente"));
    }
}