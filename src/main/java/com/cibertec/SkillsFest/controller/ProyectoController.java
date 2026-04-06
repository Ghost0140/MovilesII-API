package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.service.IProyectoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProyectoController {

    private final IProyectoService proyectoService;
    private final ObjectMapper objectMapper;

    public record CrearProyectoRequest(
            String titulo,
            String resumen,
            String descripcion,
            String repositorioUrl,
            String videoUrl,
            String demoUrl,
            List<String> tecnologias,
            Long eventoId,
            Long equipoId,
            Long usuarioId
    ) {}

    public record ActualizarProyectoRequest(
            String titulo,
            String resumen,
            String descripcion,
            String repositorioUrl,
            String videoUrl,
            String demoUrl,
            List<String> tecnologias
    ) {}

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Proyecto> proyectos = proyectoService.obtenerTodosPaginado(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyectos obtenidos exitosamente");
        response.put("data", proyectos.getContent());
        response.put("paginaActual", proyectos.getNumber());
        response.put("totalPaginas", proyectos.getTotalPages());
        response.put("totalElementos", proyectos.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Proyecto> proyecto = proyectoService.obtenerPorId(id);

        if (proyecto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Proyecto no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto obtenido",
                "data", proyecto.get()
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        List<Proyecto> proyectos = proyectoService.obtenerPorEvento(eventoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyectos del evento obtenidos",
                "data", proyectos,
                "cantidad", proyectos.size()
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearProyectoRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Proyecto nuevoProyecto = new Proyecto();
            nuevoProyecto.setTitulo(request.titulo());
            nuevoProyecto.setResumen(request.resumen());
            nuevoProyecto.setDescripcion(request.descripcion());
            nuevoProyecto.setRepositorioUrl(request.repositorioUrl());
            nuevoProyecto.setVideoUrl(request.videoUrl());
            nuevoProyecto.setDemoUrl(request.demoUrl());
            nuevoProyecto.setTecnologias(
                    request.tecnologias() != null ? objectMapper.writeValueAsString(request.tecnologias()) : "[]"
            );

            Proyecto proyectoGuardado = proyectoService.crear(
                    nuevoProyecto,
                    request.eventoId(),
                    request.equipoId(),
                    request.usuarioId()
            );

            response.put("mensaje", "Proyecto creado exitosamente");
            response.put("data", proyectoGuardado);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", "Error al crear proyecto");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActualizarProyectoRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Proyecto proyectoActualizado = new Proyecto();
            proyectoActualizado.setTitulo(request.titulo());
            proyectoActualizado.setResumen(request.resumen());
            proyectoActualizado.setDescripcion(request.descripcion());
            proyectoActualizado.setRepositorioUrl(request.repositorioUrl());
            proyectoActualizado.setVideoUrl(request.videoUrl());
            proyectoActualizado.setDemoUrl(request.demoUrl());
            proyectoActualizado.setTecnologias(
                    request.tecnologias() != null ? objectMapper.writeValueAsString(request.tecnologias()) : null
            );

            Proyecto proyecto = proyectoService.actualizar(id, proyectoActualizado);

            response.put("mensaje", "Proyecto actualizado exitosamente");
            response.put("data", proyecto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al actualizar proyecto");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<?> enviarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.enviarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto enviado para revisión",
                "data", proyecto
        ));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.aprobarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto aprobado exitosamente. Talent Radar ejecutado.",
                "data", proyecto
        ));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.rechazarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto rechazado",
                "data", proyecto
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Proyecto eliminado exitosamente"));
    }
}