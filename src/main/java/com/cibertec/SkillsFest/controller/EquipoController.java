package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.service.IEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EquipoController {

    private final IEquipoService equipoService;

    public record InscripcionEquipoRequest(
            Long eventoId,
            Long liderId,
            List<Long> miembrosIds,
            Long asesorId,
            String nombreEquipo
    ) {}

    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirEquipo(@RequestBody InscripcionEquipoRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Equipo nuevoEquipo = equipoService.inscribirEquipo(
                    request.eventoId(),
                    request.liderId(),
                    request.miembrosIds(),
                    request.asesorId(),
                    request.nombreEquipo()
            );

            response.put("mensaje", "El equipo ha sido inscrito exitosamente y está pendiente de aprobación.");
            response.put("equipo", nuevoEquipo);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            response.put("error", "Error en la inscripción");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> listarEquiposDeEvento(@PathVariable Long eventoId) {
        List<Equipo> equipos = equipoService.obtenerEquiposPorEvento(eventoId);

        if (equipos.isEmpty()) {
            return new ResponseEntity<>(Map.of("mensaje", "Aún no hay equipos inscritos en este evento."), HttpStatus.OK);
        }

        return new ResponseEntity<>(equipos, HttpStatus.OK);
    }

    @PutMapping("/{equipoId}/aprobar")
    public ResponseEntity<?> aprobarEquipo(
            @PathVariable Long equipoId,
            @RequestParam Long organizadorId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Equipo equipoAprobado = equipoService.aprobarEquipo(equipoId, organizadorId);

            response.put("mensaje", "El equipo '" + equipoAprobado.getNombre() + "' ha sido aprobado con éxito.");
            response.put("equipo", equipoAprobado);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("error", "No se pudo aprobar el equipo");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{equipoId}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long equipoId) {
        Equipo equipo = equipoService.eliminarLogico(equipoId);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Equipo eliminado lógicamente",
                "equipo", equipo
        ));
    }
}