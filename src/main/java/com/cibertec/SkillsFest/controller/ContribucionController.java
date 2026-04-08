package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.ContribucionResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.ContribucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contribuciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContribucionController {

    private final ContribucionService contribucionService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<ContribucionResponse> data = contribucionService.listarTodos()
                .stream()
                .map(ApiMapper::toContribucionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Contribuciones obtenidas correctamente",
                "data", data
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Contribucion contribucion = contribucionService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contribución no encontrada"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Contribución obtenida",
                "data", ApiMapper.toContribucionResponse(contribucion)
        ));
    }

    @GetMapping("/repositorio/{repositorioId}")
    public ResponseEntity<?> listarPorRepositorio(@PathVariable Long repositorioId) {
        List<ContribucionResponse> data = contribucionService.listarPorRepositorio(repositorioId)
                .stream()
                .map(ApiMapper::toContribucionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Contribuciones del repositorio obtenidas",
                "data", data
        ));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ContribucionResponse> data = contribucionService.listarPorUsuario(usuarioId)
                .stream()
                .map(ApiMapper::toContribucionResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Contribuciones del usuario obtenidas",
                "data", data
        ));
    }
}