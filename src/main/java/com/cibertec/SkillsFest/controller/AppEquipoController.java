package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppEquipoRequest;
import com.cibertec.SkillsFest.dto.app.AppEquipoResponse;
import com.cibertec.SkillsFest.dto.app.AppEquipoMiembroRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

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
                .map(e -> mapEquipo(e, usuario))
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
        validarNombreEquipo(request.getNombre());
        validarUsuarioPuedeInscribirseEnEvento(usuario, evento, null);

        List<Long> miembrosIds = normalizarMiembros(request.getMiembrosIds(), usuario.getId());
        validarCupoEquipo(evento, miembrosIds);
        validarMiembrosParaEvento(miembrosIds, usuario, evento, null);

        Equipo equipo = new Equipo();
        equipo.setEvento(evento);
        equipo.setSede(usuario.getSede());
        equipo.setNombre(request.getNombre().trim());
        equipo.setLider(usuario);
        equipo.setMiembros(toJsonArray(miembrosIds));
        equipo.setEstado("PENDIENTE");

        Equipo guardado = equipoRepository.save(equipo);

        return ResponseEntity.ok(mapEquipo(guardado, usuario));
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

        return ResponseEntity.ok(mapEquipo(equipo, usuario));
    }

    @PostMapping("/{id}/miembros")
    public ResponseEntity<AppEquipoResponse> agregarMiembro(
            @PathVariable Long id,
            @RequestBody AppEquipoMiembroRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        validarEquipoEditablePorLider(equipo, usuario);

        Usuario nuevoMiembro = buscarUsuarioParaAgregar(request);
        validarMiembroAgregable(equipo, nuevoMiembro);

        List<Long> miembrosIds = parseMiembros(equipo.getMiembros());
        if (!miembrosIds.contains(equipo.getLider().getId())) {
            miembrosIds.add(0, equipo.getLider().getId());
        }

        if (miembrosIds.contains(nuevoMiembro.getId())) {
            throw new RuntimeException("El estudiante ya pertenece a este equipo");
        }

        miembrosIds.add(nuevoMiembro.getId());
        validarCupoEquipo(equipo.getEvento(), miembrosIds);
        validarUsuarioPuedeInscribirseEnEvento(nuevoMiembro, equipo.getEvento(), equipo.getId());

        equipo.setMiembros(toJsonArray(miembrosIds));
        Equipo actualizado = equipoRepository.save(equipo);

        return ResponseEntity.ok(mapEquipo(actualizado, usuario));
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    public ResponseEntity<AppEquipoResponse> quitarMiembro(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        validarEquipoEditablePorLider(equipo, usuario);

        if (Objects.equals(equipo.getLider().getId(), usuarioId)) {
            throw new RuntimeException("No se puede retirar al líder del equipo");
        }

        List<Long> miembrosIds = parseMiembros(equipo.getMiembros());
        boolean eliminado = miembrosIds.removeIf(idMiembro -> Objects.equals(idMiembro, usuarioId));

        if (!eliminado) {
            throw new RuntimeException("El estudiante no pertenece a este equipo");
        }

        equipo.setMiembros(toJsonArray(miembrosIds));
        Equipo actualizado = equipoRepository.save(equipo);

        return ResponseEntity.ok(mapEquipo(actualizado, usuario));
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

    private void validarNombreEquipo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("Ingresa el nombre del equipo");
        }

        if (nombre.trim().length() < 3) {
            throw new RuntimeException("El nombre del equipo debe tener al menos 3 caracteres");
        }
    }

    private void validarEquipoEditablePorLider(Equipo equipo, Usuario usuario) {
        if ("ELIMINADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new RuntimeException("Equipo no disponible");
        }

        if (equipo.getLider() == null || !Objects.equals(equipo.getLider().getId(), usuario.getId())) {
            throw new RuntimeException("Solo el líder puede modificar los miembros del equipo");
        }

        if (!"PENDIENTE".equalsIgnoreCase(equipo.getEstado())) {
            throw new RuntimeException("Solo se pueden modificar miembros mientras el equipo está pendiente");
        }
    }

    private Usuario buscarUsuarioParaAgregar(AppEquipoMiembroRequest request) {
        if (request == null) {
            throw new RuntimeException("Debes enviar un estudiante para agregar");
        }

        if (request.getUsuarioId() != null) {
            return usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return usuarioRepository.findByEmail(request.getEmail().trim())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado por email"));
        }

        if (request.getCodigoEstudiante() != null && !request.getCodigoEstudiante().isBlank()) {
            return usuarioRepository.findByCodigoEstudiante(request.getCodigoEstudiante().trim())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado por código"));
        }

        throw new RuntimeException("Debes indicar usuarioId, email o código de estudiante");
    }

    private void validarMiembroAgregable(Equipo equipo, Usuario nuevoMiembro) {
        if (!Boolean.TRUE.equals(nuevoMiembro.getActivo())) {
            throw new RuntimeException("El estudiante no está activo");
        }

        if (nuevoMiembro.getSede() == null || equipo.getSede() == null ||
                !Objects.equals(nuevoMiembro.getSede().getId(), equipo.getSede().getId())) {
            throw new RuntimeException("El estudiante debe pertenecer a la misma sede del equipo");
        }
    }

    private void validarUsuarioPuedeInscribirseEnEvento(Usuario usuario, Evento evento, Long equipoActualId) {
        boolean yaInscrito = equipoRepository.findByEventoIdAndEstadoNot(evento.getId(), "ELIMINADO")
                .stream()
                .filter(e -> equipoActualId == null || !Objects.equals(e.getId(), equipoActualId))
                .filter(e -> !"RECHAZADO".equalsIgnoreCase(e.getEstado()))
                .anyMatch(e -> perteneceAlUsuario(e, usuario));

        if (yaInscrito) {
            throw new RuntimeException("El estudiante ya pertenece a un equipo activo en este evento");
        }
    }

    private void validarMiembrosParaEvento(List<Long> miembrosIds, Usuario lider, Evento evento, Long equipoActualId) {
        for (Long miembroId : miembrosIds) {
            Usuario miembro = usuarioRepository.findById(miembroId)
                    .orElseThrow(() -> new RuntimeException("Uno de los miembros no existe"));

            if (!Objects.equals(miembro.getId(), lider.getId())) {
                validarMiembroAgregable(new EquipoParaValidacion(evento, lider).toEquipo(), miembro);
            }

            validarUsuarioPuedeInscribirseEnEvento(miembro, evento, equipoActualId);
        }
    }

    private void validarCupoEquipo(Evento evento, List<Long> miembrosIds) {
        int maximo = evento.getMaxMiembrosEquipo() != null && evento.getMaxMiembrosEquipo() > 0
                ? evento.getMaxMiembrosEquipo()
                : 5;

        if (miembrosIds.size() > maximo) {
            throw new RuntimeException("El equipo supera el máximo de " + maximo + " integrantes");
        }
    }

    private boolean perteneceAlUsuario(Equipo equipo, Usuario usuario) {
        if (equipo.getLider() != null &&
                Objects.equals(equipo.getLider().getId(), usuario.getId())) {
            return true;
        }

        return usuarioEstaEnMiembros(equipo.getMiembros(), usuario.getId());
    }


    private AppEquipoResponse mapEquipo(Equipo equipo, Usuario usuarioActual) {
        String liderNombre = null;

        if (equipo.getLider() != null) {
            liderNombre = (equipo.getLider().getNombres() + " " + equipo.getLider().getApellidos()).trim();
        }

        List<Long> miembrosIds = parseMiembros(equipo.getMiembros());
        List<String> miembrosNombres = miembrosIds.stream()
                .map(usuarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> (u.getNombres() + " " + u.getApellidos()).trim())
                .toList();

        boolean esLider = equipo.getLider() != null &&
                usuarioActual != null &&
                Objects.equals(equipo.getLider().getId(), usuarioActual.getId());

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
                .miembrosIds(miembrosIds)
                .miembrosNombres(miembrosNombres)
                .cantidadMiembros(miembrosIds.size())
                .maxMiembrosEquipo(equipo.getEvento() != null ? equipo.getEvento().getMaxMiembrosEquipo() : null)
                .esLider(esLider)
                .creadoEn(equipo.getCreadoEn())
                .build();
    }

    private List<Long> normalizarMiembros(List<Long> miembrosIds, Long liderId) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.add(liderId);

        if (miembrosIds != null) {
            ids.addAll(miembrosIds);
        }

        return new ArrayList<>(ids);
    }

    private List<Long> parseMiembros(String miembros) {
        if (miembros == null || miembros.isBlank()) {
            return new ArrayList<>();
        }

        String normalizado = miembros
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace(" ", "");

        if (normalizado.isBlank()) {
            return new ArrayList<>();
        }

        List<Long> ids = new ArrayList<>();

        Arrays.stream(normalizado.split(","))
                .filter(id -> !id.isBlank())
                .forEach(id -> {
                    try {
                        ids.add(Long.parseLong(id));
                    } catch (NumberFormatException ignored) {
                    }
                });

        return ids;
    }

    private String toJsonArray(List<Long> ids) {
        return "[" + ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("") + "]";
    }

    private boolean usuarioEstaEnMiembros(String miembros, Long usuarioId) {
        if (usuarioId == null) {
            return false;
        }

        return parseMiembros(miembros).contains(usuarioId);
    }

    private record EquipoParaValidacion(Evento evento, Usuario lider) {
        private Equipo toEquipo() {
            Equipo equipo = new Equipo();
            equipo.setEvento(evento);
            equipo.setSede(lider.getSede());
            equipo.setLider(lider);
            return equipo;
        }
    }
}
