package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppEquipoRequest;
import com.cibertec.SkillsFest.dto.app.AppEquipoResponse;
import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/app/equipos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppEquipoController {

    private final IEquipoRepository equipoRepository;
    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;

    @GetMapping("/mis-equipos")
    public ResponseEntity<List<AppEquipoResponse>> listarMisEquipos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<AppEquipoResponse> equipos = equipoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> perteneceAlUsuario(e, usuario))
                .map(this::mapEquipo)
                .toList();

        return ResponseEntity.ok(equipos);
    }

    @PostMapping
    public ResponseEntity<AppEquipoResponse> crearEquipo(
            @RequestBody AppEquipoRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        validarEventoParaEquipo(evento);

        Equipo equipo = new Equipo();
        equipo.setEvento(evento);
        equipo.setSede(usuario.getSede());
        equipo.setNombre(request.getNombre());
        equipo.setLider(usuario);
        equipo.setMiembros("[" + usuario.getId() + "]");
        equipo.setEstado("PENDIENTE");

        Equipo guardado = equipoRepository.save(equipo);

        return ResponseEntity.ok(mapEquipo(guardado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppEquipoResponse> obtenerEquipo(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if ("ELIMINADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new RuntimeException("Equipo no disponible");
        }

        if (!perteneceAlUsuario(equipo, usuario)) {
            throw new RuntimeException("No tienes permisos para ver este equipo");
        }

        return ResponseEntity.ok(mapEquipo(equipo));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private void validarEventoParaEquipo(Evento evento) {
        LocalDate hoy = LocalDate.now();

        if (!Boolean.TRUE.equals(evento.getPermiteEquipos())) {
            throw new RuntimeException("Este evento no permite equipos");
        }

        if ("ELIMINADO".equalsIgnoreCase(evento.getEstado()) ||
                "CANCELADO".equalsIgnoreCase(evento.getEstado()) ||
                "FINALIZADO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("El evento no está disponible");
        }

        boolean inscripcionAbierta =
                !hoy.isBefore(evento.getFechaInicioInscripcion()) &&
                        !hoy.isAfter(evento.getFechaFinInscripcion());

        if (!inscripcionAbierta) {
            throw new RuntimeException("Las inscripciones para este evento no están abiertas");
        }

        if (!"PUBLICADO".equalsIgnoreCase(evento.getEstado()) &&
                !"EN_CURSO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("El evento no está habilitado para equipos");
        }
    }

    private boolean perteneceAlUsuario(Equipo equipo, Usuario usuario) {
        if (equipo.getLider() != null &&
                Objects.equals(equipo.getLider().getId(), usuario.getId())) {
            return true;
        }

        String miembros = equipo.getMiembros();

        if (miembros != null && miembros.contains(usuario.getId().toString())) {
            return true;
        }

        return false;
    }

    private AppEquipoResponse mapEquipo(Equipo equipo) {
        String liderNombre = null;

        if (equipo.getLider() != null) {
            liderNombre = (equipo.getLider().getNombres() + " " + equipo.getLider().getApellidos()).trim();
        }

        return AppEquipoResponse.builder()
                .id(equipo.getId())
                .nombre(equipo.getNombre())
                .estado(equipo.getEstado())
                .eventoId(equipo.getEvento() != null ? equipo.getEvento().getId() : null)
                .eventoNombre(equipo.getEvento() != null ? equipo.getEvento().getNombre() : null)
                .sedeId(equipo.getSede() != null ? equipo.getSede().getId() : null)
                .sedeNombre(equipo.getSede() != null ? equipo.getSede().getNombre() : null)
                .liderId(equipo.getLider() != null ? equipo.getLider().getId() : null)
                .liderNombre(liderNombre)
                .miembros(equipo.getMiembros())
                .creadoEn(equipo.getCreadoEn())
                .build();
    }
}