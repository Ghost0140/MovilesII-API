package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.ProyectoCreateRequest;
import com.cibertec.SkillsFest.dto.ProyectoEstadoRequest;
import com.cibertec.SkillsFest.dto.ProyectoResponse;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IProyectoService;
import com.cibertec.SkillsFest.service.ITalentRadarService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProyectoController {

    private final IProyectoService proyectoService;
    private final ITalentRadarService talentRadarService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Proyecto> proyectos = proyectoService.obtenerTodosPaginado(pageable);

        List<ProyectoResponse> data = proyectos.getContent()
                .stream()
                .filter(p -> !"ELIMINADO".equals(p.getEstado()))
                .map(ApiMapper::toProyectoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyectos obtenidos exitosamente",
                "data", data,
                "paginaActual", proyectos.getNumber(),
                "totalPaginas", proyectos.getTotalPages(),
                "totalElementos", proyectos.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto obtenido",
                "data", ApiMapper.toProyectoResponse(proyecto)
        ));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> obtenerPorEvento(@PathVariable Long eventoId) {
        List<ProyectoResponse> data = proyectoService.obtenerPorEvento(eventoId)
                .stream()
                .map(ApiMapper::toProyectoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyectos del evento obtenidos",
                "data", data,
                "cantidad", data.size()
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProyectoCreateRequest request) throws Exception {
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

        return new ResponseEntity<>(Map.of(
                "mensaje", "Proyecto creado exitosamente",
                "data", ApiMapper.toProyectoResponse(proyectoGuardado)
        ), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @Valid @RequestBody ProyectoEstadoRequest request) {
        Proyecto proyecto = proyectoService.cambiarEstado(id, request.estado());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado del proyecto actualizado",
                "data", ApiMapper.toProyectoResponse(proyecto)
        ));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<?> enviarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.enviarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto enviado para revisión",
                "data", ApiMapper.toProyectoResponse(proyecto)
        ));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.aprobarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto aprobado exitosamente",
                "data", ApiMapper.toProyectoResponse(proyecto)
        ));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.rechazarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proyecto rechazado",
                "data", ApiMapper.toProyectoResponse(proyecto)
        ));
    }

    @PostMapping("/{id}/analizar-radar")
    public ResponseEntity<?> analizarRadar(@PathVariable Long id) {
        talentRadarService.analizarProyecto(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Análisis de radar ejecutado correctamente",
                "proyectoId", id
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Proyecto eliminado lógicamente"));
    }
}