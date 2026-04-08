package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.EventoCreateRequest;
import com.cibertec.SkillsFest.dto.EventoEstadoRequest;
import com.cibertec.SkillsFest.dto.EventoResponse;
import com.cibertec.SkillsFest.dto.EventoUpdateRequest;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IEventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventoController {

    private final IEventoService eventoService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<EventoResponse> data = eventoService.obtenerTodos()
                .stream()
                .map(ApiMapper::toEventoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Eventos obtenidos correctamente",
                "data", data
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Evento evento = eventoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento obtenido",
                "data", ApiMapper.toEventoResponse(evento)
        ));
    }

    @GetMapping("/publicados")
    public ResponseEntity<?> listarEventosPublicados() {
        List<EventoResponse> data = eventoService.obtenerEventosPublicados()
                .stream()
                .map(ApiMapper::toEventoResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Eventos publicados obtenidos correctamente",
                "data", data
        ));
    }

    @PostMapping
    public ResponseEntity<?> crearEvento(@Valid @RequestBody EventoCreateRequest request) {
        Evento nuevoEvento = new Evento();
        nuevoEvento.setNombre(request.nombre());
        nuevoEvento.setDescripcion(request.descripcion());
        nuevoEvento.setTipo(request.tipo());
        nuevoEvento.setAlcance(request.alcance());
        nuevoEvento.setFechaInicioInscripcion(request.fechaInicioInscripcion());
        nuevoEvento.setFechaFinInscripcion(request.fechaFinInscripcion());
        nuevoEvento.setFechaEvento(request.fechaEvento());
        nuevoEvento.setMaxMiembrosEquipo(request.maxMiembrosEquipo());
        nuevoEvento.setPermiteEquipos(true);
        nuevoEvento.setPermiteVotacionPopular(false);

        Evento eventoCreado = eventoService.crearEvento(
                nuevoEvento,
                request.creadoPorId(),
                request.sedeOrganizadoraId()
        );

        return new ResponseEntity<>(Map.of(
                "mensaje", "Evento creado con éxito",
                "data", ApiMapper.toEventoResponse(eventoCreado)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody EventoUpdateRequest request) {
        Evento eventoActualizado = new Evento();
        eventoActualizado.setNombre(request.nombre());
        eventoActualizado.setDescripcion(request.descripcion());
        eventoActualizado.setTipo(request.tipo());
        eventoActualizado.setAlcance(request.alcance());
        eventoActualizado.setFechaInicioInscripcion(request.fechaInicioInscripcion());
        eventoActualizado.setFechaFinInscripcion(request.fechaFinInscripcion());
        eventoActualizado.setFechaEvento(request.fechaEvento());
        eventoActualizado.setMaxMiembrosEquipo(request.maxMiembrosEquipo());

        Evento evento = eventoService.actualizarEvento(id, eventoActualizado);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento actualizado con éxito",
                "data", ApiMapper.toEventoResponse(evento)
        ));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EventoEstadoRequest request) {
        Evento evento = eventoService.cambiarEstado(id, request.estado());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado del evento actualizado",
                "data", ApiMapper.toEventoResponse(evento)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long id) {
        Evento evento = eventoService.eliminarLogico(id);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento eliminado lógicamente",
                "data", ApiMapper.toEventoResponse(evento)
        ));
    }
}