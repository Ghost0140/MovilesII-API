package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Comentario;
import com.cibertec.SkillsFest.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<Comentario> crear(@RequestBody Comentario comentario) {
        Comentario nuevo = comentarioService.guardar(comentario);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> obtenerPorId(@PathVariable Long id) {
        return comentarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Comentario>> listarTodos() {
        return ResponseEntity.ok(comentarioService.listarTodos());
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Comentario>> listarPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(comentarioService.listarPorProyecto(proyectoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        comentarioService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}