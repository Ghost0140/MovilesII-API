package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.RepositorioResponse;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IRepositorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repositorios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RepositorioController {

    private final IRepositorioService repositorioService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<RepositorioResponse> data = repositorioService.obtenerTodos()
                .stream()
                .map(ApiMapper::toRepositorioResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Repositorios obtenidos correctamente",
                "data", data
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Repositorio repositorio = repositorioService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repositorio no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Repositorio obtenido",
                "data", ApiMapper.toRepositorioResponse(repositorio)
        ));
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<?> obtenerPorProyecto(@PathVariable Long proyectoId) {
        Repositorio repositorio = repositorioService.obtenerPorProyecto(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repositorio no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Repositorio del proyecto obtenido",
                "data", ApiMapper.toRepositorioResponse(repositorio)
        ));
    }

    @PostMapping("/proyecto/{proyectoId}/reanalizar")
    public ResponseEntity<?> reanalizar(@PathVariable Long proyectoId) {
        repositorioService.reanalizarPorProyecto(proyectoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Repositorio reanalizado correctamente",
                "proyectoId", proyectoId
        ));
    }
}