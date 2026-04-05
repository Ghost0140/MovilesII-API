package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.service.ContribucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contribuciones")
@RequiredArgsConstructor
public class ContribucionController {

    private final ContribucionService contribucionService;

    @PostMapping
    public ResponseEntity<Contribucion> crear(@RequestBody Contribucion contribucion) {
        Contribucion nueva = contribucionService.guardar(contribucion);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contribucion> obtenerPorId(@PathVariable Long id) {
        return contribucionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Contribucion>> listarTodos() {
        return ResponseEntity.ok(contribucionService.listarTodos());
    }

    @GetMapping("/repositorio/{repositorioId}")
    public ResponseEntity<List<Contribucion>> listarPorRepositorio(@PathVariable Long repositorioId) {
        return ResponseEntity.ok(contribucionService.listarPorRepositorio(repositorioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Contribucion>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(contribucionService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/repositorio/{repositorioId}/usuario/{usuarioId}")
    public ResponseEntity<Contribucion> obtenerPorRepoYUsuario(@PathVariable Long repositorioId,
                                                               @PathVariable Long usuarioId) {
        return contribucionService.obtenerPorRepositorioYUsuario(repositorioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contribucionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}