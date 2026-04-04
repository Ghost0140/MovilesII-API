package com.cibertec.SkillsFest.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.service.IEventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventoController {

	private final IEventoService eventoService;

    // --- ENDPOINT PARA ALUMNOS: Ver Cartelera ---
    // GET: http://localhost:9090/api/eventos/publicados
    @GetMapping("/publicados")
    public ResponseEntity<?> listarEventosPublicados() {
        List<Evento> eventos = eventoService.obtenerEventosPublicados();
        if (eventos.isEmpty()) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay eventos en cartelera."), HttpStatus.OK);
        }
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    // --- DTO para recibir los datos de creación ---
    public record CrearEventoRequest(
            Long sedeOrganizadoraId,
            String nombre,
            String descripcion,
            String tipo,
            String alcance,
            LocalDate fechaInicioInscripcion,
            LocalDate fechaFinInscripcion,
            LocalDate fechaEvento,
            Integer maxMiembrosEquipo,
            Long creadoPorId
    ) {}

    // --- ENDPOINT PARA PROFESORES: Crear Hackathon/Feria ---
    // POST: http://localhost:9090/api/eventos
    @PostMapping
    public ResponseEntity<?> crearEvento(@RequestBody CrearEventoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Mapeamos los datos básicos
            Evento nuevoEvento = new Evento();
            nuevoEvento.setNombre(request.nombre());
            nuevoEvento.setDescripcion(request.descripcion());
            nuevoEvento.setTipo(request.tipo());
            nuevoEvento.setAlcance(request.alcance() != null ? request.alcance() : "TODAS_SEDES");
            nuevoEvento.setFechaInicioInscripcion(request.fechaInicioInscripcion());
            nuevoEvento.setFechaFinInscripcion(request.fechaFinInscripcion());
            nuevoEvento.setFechaEvento(request.fechaEvento());
            nuevoEvento.setMaxMiembrosEquipo(request.maxMiembrosEquipo());
            
            // Valores por defecto
            nuevoEvento.setPermiteEquipos(true);
            nuevoEvento.setPermiteVotacionPopular(false);

            Evento eventoCreado = eventoService.crearEvento(nuevoEvento, request.creadoPorId(), request.sedeOrganizadoraId());
            
            response.put("mensaje", "Evento creado con éxito. Está en estado BORRADOR.");
            response.put("evento", eventoCreado);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            response.put("error", "Error al crear el evento");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
