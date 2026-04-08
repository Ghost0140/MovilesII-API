package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.EquipoEstadoRequest;
import com.cibertec.SkillsFest.dto.EquipoInscripcionRequest;
import com.cibertec.SkillsFest.dto.EquipoResponse;
import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IEquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EquipoController {

    private final IEquipoService equipoService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<EquipoResponse> data = equipoService.obtenerTodos()
                .stream()
                .map(ApiMapper::toEquipoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Equipos obtenidos correctamente",
                "data", data
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Equipo equipo = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Equipo obtenido correctamente",
                "data", ApiMapper.toEquipoResponse(equipo)
        ));
    }

    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirEquipo(@Valid @RequestBody EquipoInscripcionRequest request) {
        Equipo nuevoEquipo = equipoService.inscribirEquipo(
                request.eventoId(),
                request.liderId(),
                request.miembrosIds(),
                request.asesorId(),
                request.nombreEquipo()
        );

        return new ResponseEntity<>(Map.of(
                "mensaje", "El equipo ha sido inscrito exitosamente y está pendiente de aprobación.",
                "data", ApiMapper.toEquipoResponse(nuevoEquipo)
        ), HttpStatus.CREATED);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> listarEquiposDeEvento(@PathVariable Long eventoId) {
        List<EquipoResponse> data = equipoService.obtenerEquiposPorEvento(eventoId)
                .stream()
                .map(ApiMapper::toEquipoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Equipos del evento obtenidos correctamente",
                "data", data
        ));
    }

    @PutMapping("/{equipoId}/aprobar")
    public ResponseEntity<?> aprobarEquipo(@PathVariable Long equipoId, @RequestParam Long organizadorId) {
        Equipo equipoAprobado = equipoService.aprobarEquipo(equipoId, organizadorId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "El equipo ha sido aprobado con éxito.",
                "data", ApiMapper.toEquipoResponse(equipoAprobado)
        ));
    }

    @PatchMapping("/{equipoId}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long equipoId, @Valid @RequestBody EquipoEstadoRequest request) {
        Equipo equipo = equipoService.cambiarEstado(equipoId, request.estado());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado del equipo actualizado",
                "data", ApiMapper.toEquipoResponse(equipo)
        ));
    }

    @DeleteMapping("/{equipoId}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long equipoId) {
        Equipo equipo = equipoService.eliminarLogico(equipoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Equipo eliminado lógicamente",
                "data", ApiMapper.toEquipoResponse(equipo)
        ));
    }
}