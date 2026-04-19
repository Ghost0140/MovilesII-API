package com.cibertec.SkillsFest.controller;

import java.util.Arrays;
import com.cibertec.SkillsFest.dto.app.ActualizarRepositorioRequest;
import com.cibertec.SkillsFest.dto.app.AppProyectoRequest;
import com.cibertec.SkillsFest.dto.app.AppProyectoResponse;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IRepositorioRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cibertec.SkillsFest.dto.app.AppHistorialProyectoResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/app/proyectos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppProyectoController {

    private final IUsuarioRepository usuarioRepository;
    private final IProyectoRepository proyectoRepository;
    private final IRepositorioRepository repositorioRepository;
    private final IEventoRepository eventoRepository;
    private final IEquipoRepository equipoRepository;

    @GetMapping("/mis-proyectos")
    public ResponseEntity<List<AppProyectoResponse>> listarMisProyectos(Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<AppProyectoResponse> proyectos = proyectoRepository.findAll()
                .stream()
                .filter(p -> !"ELIMINADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> perteneceAlUsuario(p, usuario))
                .sorted((a, b) -> {
                    if (a.getFechaEnvio() == null && b.getFechaEnvio() == null) return 0;
                    if (a.getFechaEnvio() == null) return 1;
                    if (b.getFechaEnvio() == null) return -1;
                    return b.getFechaEnvio().compareTo(a.getFechaEnvio());
                })
                .map(this::mapProyecto)
                .toList();

        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppProyectoResponse> obtenerMiProyecto(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        validarProyectoDisponibleYPropio(proyecto, usuario);

        return ResponseEntity.ok(mapProyecto(proyecto));
    }

    @PostMapping
    public ResponseEntity<AppProyectoResponse> crearProyecto(
            @RequestBody AppProyectoRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        validarEventoParaCrearProyecto(evento);

        if (Boolean.TRUE.equals(evento.getPermiteEquipos())) {
            throw new RuntimeException("Este evento permite equipos. Para proyectos grupales se debe crear o seleccionar un equipo");
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setEvento(evento);
        proyecto.setUsuario(usuario);
        proyecto.setEquipo(null);

        proyecto.setTitulo(request.getTitulo());
        proyecto.setResumen(request.getResumen());
        proyecto.setDescripcion(request.getDescripcion());

        String repoUrl = normalizarRepositorioUrl(request.getRepositorioUrl());
        validarRepositorioDuplicado(repoUrl, null);
        proyecto.setRepositorioUrl(repoUrl);

        proyecto.setVideoUrl(request.getVideoUrl());
        proyecto.setDemoUrl(request.getDemoUrl());
        proyecto.setTecnologias(request.getTecnologias());

        proyecto.setEstado("BORRADOR");

        Proyecto guardado = proyectoRepository.save(proyecto);

        return ResponseEntity.ok(mapProyecto(guardado));
    }

    @PutMapping("/{id}/repositorio")
    public ResponseEntity<AppProyectoResponse> actualizarRepositorio(
            @PathVariable Long id,
            @RequestBody ActualizarRepositorioRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        validarProyectoDisponibleYPropio(proyecto, usuario);

        if ("APROBADO".equalsIgnoreCase(proyecto.getEstado())) {
            throw new RuntimeException("No se puede cambiar el repositorio de un proyecto aprobado");
        }

        String repoUrl = normalizarRepositorioUrl(request.getRepositorioUrl());

        if (repoUrl == null || repoUrl.isBlank()) {
            throw new RuntimeException("Debe ingresar una URL de repositorio");
        }

        validarRepositorioDuplicado(repoUrl, proyecto.getId());

        proyecto.setRepositorioUrl(repoUrl);

        Proyecto actualizado = proyectoRepository.save(proyecto);

        return ResponseEntity.ok(mapProyecto(actualizado));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<AppProyectoResponse> enviarProyecto(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        validarProyectoDisponibleYPropio(proyecto, usuario);

        if (proyecto.getRepositorioUrl() == null || proyecto.getRepositorioUrl().isBlank()) {
            throw new RuntimeException("Debe registrar un repositorio antes de enviar el proyecto");
        }

        if ("APROBADO".equalsIgnoreCase(proyecto.getEstado())) {
            throw new RuntimeException("El proyecto ya fue aprobado");
        }

        if ("ENVIADO".equalsIgnoreCase(proyecto.getEstado())) {
            throw new RuntimeException("El proyecto ya fue enviado");
        }

        proyecto.setEstado("ENVIADO");
        proyecto.setFechaEnvio(LocalDateTime.now());

        Proyecto enviado = proyectoRepository.save(proyecto);

        return ResponseEntity.ok(mapProyecto(enviado));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private void validarEventoParaCrearProyecto(Evento evento) {
        LocalDate hoy = LocalDate.now();

        if ("ELIMINADO".equalsIgnoreCase(evento.getEstado()) ||
                "CANCELADO".equalsIgnoreCase(evento.getEstado()) ||
                "FINALIZADO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("El evento no está disponible para inscripción");
        }

        boolean inscripcionAbierta =
                !hoy.isBefore(evento.getFechaInicioInscripcion()) &&
                        !hoy.isAfter(evento.getFechaFinInscripcion());

        if (!inscripcionAbierta) {
            throw new RuntimeException("Las inscripciones para este evento no están abiertas");
        }

        if (!"PUBLICADO".equalsIgnoreCase(evento.getEstado()) &&
                !"EN_CURSO".equalsIgnoreCase(evento.getEstado())) {
            throw new RuntimeException("El evento no está habilitado para recibir proyectos");
        }
    }

    private void validarProyectoDisponibleYPropio(Proyecto proyecto, Usuario usuario) {
        if ("ELIMINADO".equalsIgnoreCase(proyecto.getEstado())) {
            throw new RuntimeException("Proyecto no disponible");
        }

        if (!perteneceAlUsuario(proyecto, usuario)) {
            throw new RuntimeException("No tienes permisos para este proyecto");
        }
    }

    private boolean perteneceAlUsuario(Proyecto proyecto, Usuario usuario) {
        if (proyecto.getUsuario() != null &&
                Objects.equals(proyecto.getUsuario().getId(), usuario.getId())) {
            return true;
        }

        if (proyecto.getEquipo() != null) {
            if (proyecto.getEquipo().getLider() != null &&
                    Objects.equals(proyecto.getEquipo().getLider().getId(), usuario.getId())) {
                return true;
            }

            return usuarioEstaEnMiembros(proyecto.getEquipo().getMiembros(), usuario.getId());
        }

        return false;
    }

    private boolean usuarioEstaEnMiembros(String miembros, Long usuarioId) {
        if (miembros == null || miembros.isBlank() || usuarioId == null) {
            return false;
        }

        String normalizado = miembros
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");

        return Arrays.stream(normalizado.split(","))
                .anyMatch(id -> id.equals(usuarioId.toString()));
    }

    private void validarRepositorioDuplicado(String repoUrl, Long proyectoIdActual) {
        if (repoUrl == null || repoUrl.isBlank()) {
            return;
        }

        boolean existe;

        if (proyectoIdActual == null) {
            existe = proyectoRepository.existsByRepositorioUrlAndEstadoNot(repoUrl, "ELIMINADO");
        } else {
            existe = proyectoRepository.existsByRepositorioUrlAndIdNotAndEstadoNot(
                    repoUrl,
                    proyectoIdActual,
                    "ELIMINADO"
            );
        }

        if (existe) {
            throw new RuntimeException("Este repositorio ya está asociado a otro proyecto activo");
        }
    }

    private String normalizarRepositorioUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        String limpia = url.trim();

        if (limpia.endsWith("/")) {
            limpia = limpia.substring(0, limpia.length() - 1);
        }

        return limpia;
    }

    private AppProyectoResponse mapProyecto(Proyecto proyecto) {
        Repositorio repo = repositorioRepository.findByProyectoId(proyecto.getId())
                .orElse(null);

        return AppProyectoResponse.builder()
                .id(proyecto.getId())
                .titulo(proyecto.getTitulo())
                .resumen(proyecto.getResumen())
                .descripcion(proyecto.getDescripcion())
                .estado(proyecto.getEstado())
                .repositorioUrl(proyecto.getRepositorioUrl())
                .videoUrl(proyecto.getVideoUrl())
                .demoUrl(proyecto.getDemoUrl())
                .tecnologias(proyecto.getTecnologias())
                .tipoParticipacion(proyecto.getEquipo() != null ? "EQUIPO" : "INDIVIDUAL")
                .eventoId(proyecto.getEvento() != null ? proyecto.getEvento().getId() : null)
                .eventoNombre(proyecto.getEvento() != null ? proyecto.getEvento().getNombre() : null)
                .eventoEstado(proyecto.getEvento() != null ? proyecto.getEvento().getEstado() : null)
                .permiteEquipos(proyecto.getEvento() != null ? proyecto.getEvento().getPermiteEquipos() : null)
                .equipoId(proyecto.getEquipo() != null ? proyecto.getEquipo().getId() : null)
                .equipoNombre(proyecto.getEquipo() != null ? proyecto.getEquipo().getNombre() : null)
                .equipoEstado(proyecto.getEquipo() != null ? proyecto.getEquipo().getEstado() : null)
                .fechaEnvio(proyecto.getFechaEnvio())
                .estadoRadar(repo != null ? repo.getEstadoAnalisis() : "PENDIENTE")
                .totalCommits(repo != null ? repo.getTotalCommits() : 0)
                .contributorsGithub(repo != null ? repo.getContributorsGithub() : 0)
                .usuariosMapeados(repo != null ? repo.getUsuariosMapeados() : 0)
                .contribucionesGeneradas(repo != null ? repo.getContribucionesGeneradas() : 0)
                .build();
    }

    @PostMapping("/equipo/{equipoId}")
    public ResponseEntity<AppProyectoResponse> crearProyectoPorEquipo(
            @PathVariable Long equipoId,
            @RequestBody AppProyectoRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if ("ELIMINADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new RuntimeException("Equipo no disponible");
        }

        if (equipo.getLider() == null || !Objects.equals(equipo.getLider().getId(), usuario.getId())) {
            throw new RuntimeException("Solo el líder del equipo puede crear el proyecto");
        }

        if (!"APROBADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new RuntimeException("El equipo debe estar aprobado antes de crear un proyecto");
        }

        Evento evento = equipo.getEvento();

        if (evento == null) {
            throw new RuntimeException("El equipo no tiene evento asociado");
        }

        validarEventoParaCrearProyecto(evento);

        if (!Boolean.TRUE.equals(evento.getPermiteEquipos())) {
            throw new RuntimeException("Este evento es individual y no permite proyectos por equipo");
        }

        boolean yaTieneProyecto = proyectoRepository.existsByEquipoIdAndEstadoNot(
                equipo.getId(),
                "ELIMINADO"
        );

        if (yaTieneProyecto) {
            throw new RuntimeException("Este equipo ya tiene un proyecto registrado");
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setEvento(evento);
        proyecto.setEquipo(equipo);
        proyecto.setUsuario(null);

        proyecto.setTitulo(request.getTitulo());
        proyecto.setResumen(request.getResumen());
        proyecto.setDescripcion(request.getDescripcion());

        String repoUrl = normalizarRepositorioUrl(request.getRepositorioUrl());
        validarRepositorioDuplicado(repoUrl, null);
        proyecto.setRepositorioUrl(repoUrl);

        proyecto.setVideoUrl(request.getVideoUrl());
        proyecto.setDemoUrl(request.getDemoUrl());
        proyecto.setTecnologias(request.getTecnologias());

        proyecto.setEstado("BORRADOR");

        Proyecto guardado = proyectoRepository.save(proyecto);

        return ResponseEntity.ok(mapProyecto(guardado));
    }

    @GetMapping("/historial")
    public ResponseEntity<AppHistorialProyectoResponse> obtenerHistorialProyectos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<Proyecto> proyectos = proyectoRepository.findAll()
                .stream()
                .filter(p -> !"ELIMINADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> perteneceAlUsuario(p, usuario))
                .sorted((a, b) -> {
                    if (a.getFechaEnvio() == null && b.getFechaEnvio() == null) return 0;
                    if (a.getFechaEnvio() == null) return 1;
                    if (b.getFechaEnvio() == null) return -1;
                    return b.getFechaEnvio().compareTo(a.getFechaEnvio());
                })
                .toList();

        List<AppProyectoResponse> activos = proyectos.stream()
                .filter(p ->
                        "BORRADOR".equalsIgnoreCase(p.getEstado()) ||
                                "ENVIADO".equalsIgnoreCase(p.getEstado()) ||
                                "OBSERVADO".equalsIgnoreCase(p.getEstado())
                )
                .map(this::mapProyecto)
                .toList();

        List<AppProyectoResponse> historicos = proyectos.stream()
                .filter(p ->
                        "APROBADO".equalsIgnoreCase(p.getEstado()) ||
                                "RECHAZADO".equalsIgnoreCase(p.getEstado())
                )
                .map(this::mapProyecto)
                .toList();

        int totalIndividuales = (int) proyectos.stream()
                .filter(p -> p.getEquipo() == null)
                .count();

        int totalGrupales = (int) proyectos.stream()
                .filter(p -> p.getEquipo() != null)
                .count();

        int totalAprobados = (int) proyectos.stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstado()))
                .count();

        int totalEnviados = (int) proyectos.stream()
                .filter(p -> "ENVIADO".equalsIgnoreCase(p.getEstado()))
                .count();

        AppHistorialProyectoResponse response = AppHistorialProyectoResponse.builder()
                .usuarioId(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .totalProyectos(proyectos.size())
                .totalIndividuales(totalIndividuales)
                .totalGrupales(totalGrupales)
                .totalAprobados(totalAprobados)
                .totalEnviados(totalEnviados)
                .activos(activos)
                .historicos(historicos)
                .build();

        return ResponseEntity.ok(response);
    }


}