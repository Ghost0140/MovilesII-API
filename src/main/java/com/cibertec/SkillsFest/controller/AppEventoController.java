package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppEventoResponse;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/app/eventos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppEventoController {

    private final IEventoRepository eventoRepository;

    @GetMapping("/activos")
    public ResponseEntity<List<AppEventoResponse>> listarEventosActivos() {
        LocalDate hoy = LocalDate.now();

        List<AppEventoResponse> eventos = eventoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> !"CANCELADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> !"FINALIZADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> "PUBLICADO".equalsIgnoreCase(e.getEstado()) || "EN_CURSO".equalsIgnoreCase(e.getEstado()))
                .sorted(Comparator.comparing(Evento::getFechaEvento))
                .map(e -> mapEvento(e, hoy))
                .toList();

        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/pasados")
    public ResponseEntity<List<AppEventoResponse>> listarEventosPasados() {
        LocalDate hoy = LocalDate.now();

        List<AppEventoResponse> eventos = eventoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> "FINALIZADO".equalsIgnoreCase(e.getEstado()) || e.getFechaEvento().isBefore(hoy))
                .sorted((a, b) -> b.getFechaEvento().compareTo(a.getFechaEvento()))
                .map(e -> mapEvento(e, hoy))
                .toList();

        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppEventoResponse> obtenerDetalleEvento(@PathVariable Long id) {
        LocalDate hoy = LocalDate.now();

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if ("ELIMINADO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("Evento no disponible");
        }

        return ResponseEntity.ok(mapEvento(evento, hoy));
    }

    private AppEventoResponse mapEvento(Evento evento, LocalDate hoy) {
        boolean inscripcionAbierta =
                !hoy.isBefore(evento.getFechaInicioInscripcion()) &&
                        !hoy.isAfter(evento.getFechaFinInscripcion()) &&
                        ("PUBLICADO".equalsIgnoreCase(evento.getEstado()) || "EN_CURSO".equalsIgnoreCase(evento.getEstado()));

        boolean eventoActivo =
                "EN_CURSO".equalsIgnoreCase(evento.getEstado()) ||
                        ("PUBLICADO".equalsIgnoreCase(evento.getEstado()) && !evento.getFechaEvento().isBefore(hoy));

        boolean eventoFinalizado =
                "FINALIZADO".equalsIgnoreCase(evento.getEstado()) ||
                        evento.getFechaEvento().isBefore(hoy);

        return AppEventoResponse.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .tipo(evento.getTipo())
                .alcance(evento.getAlcance())
                .estado(evento.getEstado())
                .permiteEquipos(evento.getPermiteEquipos())
                .maxMiembrosEquipo(evento.getMaxMiembrosEquipo())
                .permiteVotacionPopular(evento.getPermiteVotacionPopular())
                .fechaInicioInscripcion(evento.getFechaInicioInscripcion())
                .fechaFinInscripcion(evento.getFechaFinInscripcion())
                .fechaEvento(evento.getFechaEvento())
                .sedeOrganizadora(evento.getSedeOrganizadora() != null ? evento.getSedeOrganizadora().getNombre() : null)
                .bannerUrl(evento.getBannerUrl())
                .inscripcionAbierta(inscripcionAbierta)
                .eventoActivo(eventoActivo)
                .eventoFinalizado(eventoFinalizado)
                .build();
    }
}