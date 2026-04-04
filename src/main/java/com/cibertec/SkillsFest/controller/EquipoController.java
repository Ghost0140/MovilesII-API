package com.cibertec.SkillsFest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.serviceImpl.EquipoServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*") // ¡VITAL! Evita el temido error de CORS cuando React intente conectarse
@RequiredArgsConstructor
public class EquipoController {

	private final EquipoServiceImpl equipoService;

    // --- EL DTO (Data Transfer Object) ---
    // Usamos 'record' de Java 21. Es la forma más limpia de atrapar el JSON de React.
    // React enviará un JSON con esta estructura exacta.
    public record InscripcionEquipoRequest(
            Long eventoId,
            Long liderId,
            List<Long> miembrosIds,
            Long asesorId,
            String nombreEquipo
    ) {}

    // --- EL ENDPOINT ---
    // POST: http://localhost:8080/api/equipos/inscribir
    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirEquipo(@RequestBody InscripcionEquipoRequest request) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // Llamamos a la lógica de negocio que creamos en el Service
            Equipo nuevoEquipo = equipoService.inscribirEquipo(
                    request.eventoId(),
                    request.liderId(),
                    request.miembrosIds(),
                    request.asesorId(),
                    request.nombreEquipo()
            );

            // Si todo sale bien, respondemos con código 201 (CREATED) y los datos del equipo
            response.put("mensaje", "El equipo ha sido inscrito exitosamente y está pendiente de aprobación.");
            response.put("equipo", nuevoEquipo);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Si alguna regla de Cibertec se rompe (ej. fecha inválida), 
            // atrapamos el error y le enviamos un 400 (BAD REQUEST) a React
            response.put("error", "Error en la inscripción");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
 // --- ENDPOINT PARA EL PROFESOR: Ver todos los equipos de su evento ---
    // GET: http://localhost:9090/api/equipos/evento/{eventoId}
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> listarEquiposDeEvento(@PathVariable Long eventoId) {
        List<Equipo> equipos = equipoService.obtenerEquiposPorEvento(eventoId);
        
        if (equipos.isEmpty()) {
            return new ResponseEntity<>(Map.of("mensaje", "Aún no hay equipos inscritos en este evento."), HttpStatus.OK);
        }
        return new ResponseEntity<>(equipos, HttpStatus.OK);
    }

    // --- ENDPOINT PARA EL PROFESOR: Aprobar un equipo ---
    // PUT: http://localhost:9090/api/equipos/{equipoId}/aprobar?organizadorId={id}
    @PutMapping("/{equipoId}/aprobar")
    public ResponseEntity<?> aprobarEquipo(
            @PathVariable Long equipoId, 
            @RequestParam Long organizadorId) { // Recibimos el ID del profe por la URL temporalmente
        
        Map<String, Object> response = new HashMap<>();

        try {
            Equipo equipoAprobado = equipoService.aprobarEquipo(equipoId, organizadorId);
            
            response.put("mensaje", "El equipo '" + equipoAprobado.getNombre() + "' ha sido aprobado con éxito.");
            response.put("equipo", equipoAprobado);
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK

        } catch (RuntimeException e) {
            response.put("error", "No se pudo aprobar el equipo");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
