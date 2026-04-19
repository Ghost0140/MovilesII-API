package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppDashboardResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IPortafolioPublicoRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppDashboardController {

    private final IUsuarioRepository usuarioRepository;
    private final IEventoRepository eventoRepository;
    private final IProyectoRepository proyectoRepository;
    private final IContribucionRepository contribucionRepository;
    private final IPortafolioPublicoRepository portafolioRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<AppDashboardResponse> obtenerDashboard(Authentication authentication) {

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        List<Evento> eventosActivos = eventoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> !"CANCELADO".equalsIgnoreCase(e.getEstado()))
                .filter(e -> !"FINALIZADO".equalsIgnoreCase(e.getEstado()))
                .sorted(Comparator.comparing(Evento::getFechaEvento))
                .limit(5)
                .toList();

        List<Proyecto> misProyectos = proyectoRepository.findAll()
                .stream()
                .filter(p -> !"ELIMINADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> perteneceAlUsuario(p, usuario))
                .sorted((a, b) -> {
                    if (a.getFechaEnvio() == null && b.getFechaEnvio() == null) return 0;
                    if (a.getFechaEnvio() == null) return 1;
                    if (b.getFechaEnvio() == null) return -1;
                    return b.getFechaEnvio().compareTo(a.getFechaEnvio());
                })
                .limit(5)
                .toList();

        PortafolioPublico portafolio = portafolioRepository
                .findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElse(null);

        List<Contribucion> contribuciones = contribucionRepository.findByUsuarioId(usuario.getId());

        AppDashboardResponse.RadarResumen radarResumen = construirRadarResumen(portafolio, contribuciones);

        List<AppDashboardResponse.EventoResumen> eventosResponse = eventosActivos.stream()
                .map(this::mapEvento)
                .toList();

        List<AppDashboardResponse.ProyectoResumen> proyectosResponse = misProyectos.stream()
                .map(p -> mapProyecto(p, usuario))
                .toList();

        int totalEnviados = (int) misProyectos.stream()
                .filter(p -> "ENVIADO".equalsIgnoreCase(p.getEstado()))
                .count();

        int totalAprobados = (int) misProyectos.stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstado()))
                .count();

        AppDashboardResponse response = AppDashboardResponse.builder()
                .usuario(AppDashboardResponse.UsuarioResumen.builder()
                        .id(usuario.getId())
                        .nombres(usuario.getNombres())
                        .apellidos(usuario.getApellidos())
                        .email(usuario.getEmail())
                        .roles(usuario.getRoles())
                        .githubUsername(usuario.getGithubUsername())
                        .sede(usuario.getSede() != null ? usuario.getSede().getNombre() : null)
                        .carrera(usuario.getCarrera())
                        .ciclo(usuario.getCiclo())
                        .build())
                .radar(radarResumen)
                .eventosActivos(eventosResponse)
                .misProyectos(proyectosResponse)
                .resumen(AppDashboardResponse.ResumenGeneral.builder()
                        .totalEventosActivos(eventosResponse.size())
                        .totalMisProyectos(proyectosResponse.size())
                        .totalProyectosEnviados(totalEnviados)
                        .totalProyectosAprobados(totalAprobados)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    private boolean perteneceAlUsuario(Proyecto proyecto, Usuario usuario) {
        if (proyecto.getUsuario() != null &&
                Objects.equals(proyecto.getUsuario().getId(), usuario.getId())) {
            return true;
        }

        if (proyecto.getEquipo() != null &&
                proyecto.getEquipo().getLider() != null &&
                Objects.equals(proyecto.getEquipo().getLider().getId(), usuario.getId())) {
            return true;
        }

        return false;
    }

    private AppDashboardResponse.RadarResumen construirRadarResumen(
            PortafolioPublico portafolio,
            List<Contribucion> contribuciones
    ) {
        if (contribuciones == null || contribuciones.isEmpty()) {
            return AppDashboardResponse.RadarResumen.builder()
                    .estado("PENDIENTE")
                    .mensaje("Todavía no tienes análisis de Talent Radar")
                    .frontend(valorOrZero(portafolio != null ? portafolio.getRadarFrontend() : null))
                    .backend(valorOrZero(portafolio != null ? portafolio.getRadarBackend() : null))
                    .bd(valorOrZero(portafolio != null ? portafolio.getRadarBd() : null))
                    .mobile(valorOrZero(portafolio != null ? portafolio.getRadarMobile() : null))
                    .testing(valorOrZero(portafolio != null ? portafolio.getRadarTesting() : null))
                    .totalCommits(0)
                    .contributorsGithub(0)
                    .usuariosMapeados(0)
                    .contribucionesGeneradas(0)
                    .build();
        }

        Contribucion ultima = contribuciones.stream()
                .max(Comparator.comparing(Contribucion::getAnalizadoEn))
                .orElse(null);

        if (ultima == null || ultima.getRepositorio() == null) {
            return AppDashboardResponse.RadarResumen.builder()
                    .estado("PENDIENTE")
                    .mensaje("Radar pendiente")
                    .frontend(BigDecimal.ZERO)
                    .backend(BigDecimal.ZERO)
                    .bd(BigDecimal.ZERO)
                    .mobile(BigDecimal.ZERO)
                    .testing(BigDecimal.ZERO)
                    .totalCommits(0)
                    .contributorsGithub(0)
                    .usuariosMapeados(0)
                    .contribucionesGeneradas(0)
                    .build();
        }

        Repositorio repo = ultima.getRepositorio();

        return AppDashboardResponse.RadarResumen.builder()
                .estado(repo.getEstadoAnalisis())
                .mensaje(generarMensajeRadar(repo.getEstadoAnalisis()))
                .frontend(valorOrZero(portafolio != null ? portafolio.getRadarFrontend() : null))
                .backend(valorOrZero(portafolio != null ? portafolio.getRadarBackend() : null))
                .bd(valorOrZero(portafolio != null ? portafolio.getRadarBd() : null))
                .mobile(valorOrZero(portafolio != null ? portafolio.getRadarMobile() : null))
                .testing(valorOrZero(portafolio != null ? portafolio.getRadarTesting() : null))
                .totalCommits(repo.getTotalCommits())
                .contributorsGithub(repo.getContributorsGithub())
                .usuariosMapeados(repo.getUsuariosMapeados())
                .contribucionesGeneradas(repo.getContribucionesGeneradas())
                .ultimoAnalisis(repo.getUltimoAnalisis())
                .build();
    }

    private AppDashboardResponse.EventoResumen mapEvento(Evento evento) {
        return AppDashboardResponse.EventoResumen.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .tipo(evento.getTipo())
                .alcance(evento.getAlcance())
                .estado(evento.getEstado())
                .permiteEquipos(evento.getPermiteEquipos())
                .maxMiembrosEquipo(evento.getMaxMiembrosEquipo())
                .fechaInicioInscripcion(evento.getFechaInicioInscripcion())
                .fechaFinInscripcion(evento.getFechaFinInscripcion())
                .fechaEvento(evento.getFechaEvento())
                .build();
    }

    private AppDashboardResponse.ProyectoResumen mapProyecto(Proyecto proyecto, Usuario usuario) {
        return AppDashboardResponse.ProyectoResumen.builder()
                .id(proyecto.getId())
                .titulo(proyecto.getTitulo())
                .resumen(proyecto.getResumen())
                .estado(proyecto.getEstado())
                .repositorioUrl(proyecto.getRepositorioUrl())
                .tipoParticipacion(proyecto.getEquipo() != null ? "EQUIPO" : "INDIVIDUAL")
                .eventoId(proyecto.getEvento() != null ? proyecto.getEvento().getId() : null)
                .eventoNombre(proyecto.getEvento() != null ? proyecto.getEvento().getNombre() : null)
                .fechaEnvio(proyecto.getFechaEnvio())
                .build();
    }

    private BigDecimal valorOrZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String generarMensajeRadar(String estado) {
        if (estado == null) {
            return "Estado de Radar no disponible";
        }

        return switch (estado) {
            case "COMPLETADO" -> "Talent Radar completado correctamente";
            case "INCOMPLETO" -> "Talent Radar incompleto";
            case "ERROR" -> "Error durante el análisis del Radar";
            case "EN_PROCESO" -> "Talent Radar en proceso";
            case "PENDIENTE" -> "Talent Radar pendiente";
            default -> "Estado de Radar desconocido";
        };
    }
}