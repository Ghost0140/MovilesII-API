package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.ResultadoCalculoRequest;
import com.cibertec.SkillsFest.dto.ResultadoResponse;
import com.cibertec.SkillsFest.entity.Resultado;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IResultadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResultadoController {

    private final IResultadoService resultadoService;

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Resultado> resultados = resultadoService.obtenerTodosPaginado(pageable);

        List<ResultadoResponse> data = resultados.getContent()
                .stream()
                .filter(r -> !"ELIMINADO".equals(r.getEstado()))
                .map(ApiMapper::toResultadoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultados obtenidos exitosamente",
                "data", data,
                "paginaActual", resultados.getNumber(),
                "totalPaginas", resultados.getTotalPages(),
                "totalElementos", resultados.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Resultado resultado = resultadoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultado obtenido",
                "data", ApiMapper.toResultadoResponse(resultado)
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        List<ResultadoResponse> data = resultadoService.obtenerPorEvento(eventoId)
                .stream()
                .map(ApiMapper::toResultadoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultados del evento obtenidos",
                "data", data
        ));
    }

    @PostMapping("/calcular")
    public ResponseEntity<?> calcular(@Valid @RequestBody ResultadoCalculoRequest request) {
        Resultado resultado = resultadoService.calcularResultados(request.eventoId(), request.proyectoId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resultado calculado exitosamente",
                "data", ApiMapper.toResultadoResponse(resultado)
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