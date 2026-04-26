package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.MiRadarResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.repository.IPortafolioPublicoRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IRepositorioRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppRadarController {

    private final IUsuarioRepository usuarioRepository;
    private final IContribucionRepository contribucionRepository;
    private final IPortafolioPublicoRepository portafolioRepository;
    private final IProyectoRepository proyectoRepository;
    private final IRepositorioRepository repositorioRepository;

    @GetMapping("/mi-radar")
    public ResponseEntity<MiRadarResponse> obtenerMiRadar(Authentication authentication) {

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        List<Contribucion> contribuciones = contribucionRepository.findByUsuarioId(usuario.getId());

        PortafolioPublico portafolio = portafolioRepository
                .findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElse(null);

        if (contribuciones.isEmpty()) {
            MiRadarResponse response = MiRadarResponse.builder()
                    .usuarioId(usuario.getId())
                    .nombres(usuario.getNombres())
                    .apellidos(usuario.getApellidos())
                    .email(usuario.getEmail())
                    .githubUsername(usuario.getGithubUsername())
                    .estado("PENDIENTE")
                    .mensaje("Todavía no tienes análisis de Talent Radar")
                    .radarFrontend(valorOrZero(portafolio != null ? portafolio.getRadarFrontend() : null))
                    .radarBackend(valorOrZero(portafolio != null ? portafolio.getRadarBackend() : null))
                    .radarBd(valorOrZero(portafolio != null ? portafolio.getRadarBd() : null))
                    .radarMobile(valorOrZero(portafolio != null ? portafolio.getRadarMobile() : null))
                    .radarTesting(valorOrZero(portafolio != null ? portafolio.getRadarTesting() : null))
                    .build();

            return ResponseEntity.ok(response);
        }

        Contribucion ultimaContribucion = contribuciones.stream()
                .max(Comparator.comparing(Contribucion::getAnalizadoEn))
                .orElseThrow(() -> new RuntimeException("No se pudo obtener la última contribución"));

        Repositorio repositorio = ultimaContribucion.getRepositorio();

        MiRadarResponse response = MiRadarResponse.builder()
                .usuarioId(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .githubUsername(usuario.getGithubUsername())

                .estado(repositorio.getEstadoAnalisis())
                .mensaje(generarMensaje(repositorio.getEstadoAnalisis()))

                .repositorioId(repositorio.getId())
                .repositorioUrl(repositorio.getUrl())
                .totalCommits(repositorio.getTotalCommits())
                .contributorsGithub(repositorio.getContributorsGithub())
                .usuariosMapeados(repositorio.getUsuariosMapeados())
                .contribucionesGeneradas(repositorio.getContribucionesGeneradas())
                .ultimoAnalisis(repositorio.getUltimoAnalisis())

                .radarFrontend(valorOrZero(portafolio != null ? portafolio.getRadarFrontend() : null))
                .radarBackend(valorOrZero(portafolio != null ? portafolio.getRadarBackend() : null))
                .radarBd(valorOrZero(portafolio != null ? portafolio.getRadarBd() : null))
                .radarMobile(valorOrZero(portafolio != null ? portafolio.getRadarMobile() : null))
                .radarTesting(valorOrZero(portafolio != null ? portafolio.getRadarTesting() : null))

                .commitsUsuario(ultimaContribucion.getTotalCommits())
                .lineasUsuario(ultimaContribucion.getTotalLineas())

                .scoreFrontend(valorOrZero(ultimaContribucion.getScoreFrontend()))
                .scoreBackend(valorOrZero(ultimaContribucion.getScoreBackend()))
                .scoreBd(valorOrZero(ultimaContribucion.getScoreBd()))
                .scoreMobile(valorOrZero(ultimaContribucion.getScoreMobile()))
                .scoreTesting(valorOrZero(ultimaContribucion.getScoreTesting()))

                .tecnologiasDetectadas(ultimaContribucion.getTecnologiasDetectadas())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/radar/proyecto/{proyectoId}")
    public ResponseEntity<MiRadarResponse> obtenerRadarPorProyecto(
            @PathVariable Long proyectoId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        validarAccesoProyecto(proyecto, usuario);

        PortafolioPublico portafolio = portafolioRepository
                .findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElse(null);

        Repositorio repositorio = repositorioRepository.findByProyectoId(proyectoId)
                .orElse(null);

        Contribucion contribucion = repositorio != null
                ? contribucionRepository.findByRepositorioIdAndUsuarioId(repositorio.getId(), usuario.getId()).orElse(null)
                : null;

        MiRadarResponse.MiRadarResponseBuilder builder = MiRadarResponse.builder()
                .proyectoId(proyecto.getId())
                .proyectoTitulo(proyecto.getTitulo())
                .eventoNombre(proyecto.getEvento() != null ? proyecto.getEvento().getNombre() : null)
                .usuarioId(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .githubUsername(usuario.getGithubUsername())
                .radarFrontend(valorOrZero(portafolio != null ? portafolio.getRadarFrontend() : null))
                .radarBackend(valorOrZero(portafolio != null ? portafolio.getRadarBackend() : null))
                .radarBd(valorOrZero(portafolio != null ? portafolio.getRadarBd() : null))
                .radarMobile(valorOrZero(portafolio != null ? portafolio.getRadarMobile() : null))
                .radarTesting(valorOrZero(portafolio != null ? portafolio.getRadarTesting() : null));

        if (repositorio == null) {
            return ResponseEntity.ok(builder
                    .estado("PENDIENTE")
                    .mensaje("El proyecto todavía no tiene análisis de Talent Radar")
                    .repositorioUrl(proyecto.getRepositorioUrl())
                    .totalCommits(0)
                    .contributorsGithub(0)
                    .usuariosMapeados(0)
                    .contribucionesGeneradas(0)
                    .commitsUsuario(0)
                    .lineasUsuario(0)
                    .scoreFrontend(BigDecimal.ZERO)
                    .scoreBackend(BigDecimal.ZERO)
                    .scoreBd(BigDecimal.ZERO)
                    .scoreMobile(BigDecimal.ZERO)
                    .scoreTesting(BigDecimal.ZERO)
                    .build());
        }

        return ResponseEntity.ok(builder
                .estado(repositorio.getEstadoAnalisis())
                .mensaje(generarMensaje(repositorio.getEstadoAnalisis()))
                .repositorioId(repositorio.getId())
                .repositorioUrl(repositorio.getUrl())
                .totalCommits(repositorio.getTotalCommits())
                .contributorsGithub(repositorio.getContributorsGithub())
                .usuariosMapeados(repositorio.getUsuariosMapeados())
                .contribucionesGeneradas(repositorio.getContribucionesGeneradas())
                .ultimoAnalisis(repositorio.getUltimoAnalisis())
                .commitsUsuario(contribucion != null ? contribucion.getTotalCommits() : 0)
                .lineasUsuario(contribucion != null ? contribucion.getTotalLineas() : 0)
                .scoreFrontend(valorOrZero(contribucion != null ? contribucion.getScoreFrontend() : null))
                .scoreBackend(valorOrZero(contribucion != null ? contribucion.getScoreBackend() : null))
                .scoreBd(valorOrZero(contribucion != null ? contribucion.getScoreBd() : null))
                .scoreMobile(valorOrZero(contribucion != null ? contribucion.getScoreMobile() : null))
                .scoreTesting(valorOrZero(contribucion != null ? contribucion.getScoreTesting() : null))
                .tecnologiasDetectadas(contribucion != null ? contribucion.getTecnologiasDetectadas() : null)
                .build());
    }

    private BigDecimal valorOrZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String generarMensaje(String estado) {
        if (estado == null) {
            return "Estado de análisis no disponible";
        }

        return switch (estado) {
            case "COMPLETADO" -> "Talent Radar completado correctamente";
            case "INCOMPLETO" -> "Talent Radar incompleto. Algunos contributors no fueron vinculados";
            case "ERROR" -> "Ocurrió un error durante el análisis del Radar";
            case "EN_PROCESO" -> "Talent Radar en proceso";
            case "PENDIENTE" -> "Talent Radar pendiente";
            default -> "Estado de análisis desconocido";
        };
    }

    private void validarAccesoProyecto(Proyecto proyecto, Usuario usuario) {
        if (proyecto.getUsuario() != null &&
                Objects.equals(proyecto.getUsuario().getId(), usuario.getId())) {
            return;
        }

        if (proyecto.getEquipo() != null) {
            if (proyecto.getEquipo().getLider() != null &&
                    Objects.equals(proyecto.getEquipo().getLider().getId(), usuario.getId())) {
                return;
            }

            if (parseMiembros(proyecto.getEquipo().getMiembros()).contains(usuario.getId())) {
                return;
            }
        }

        throw new RuntimeException("No tienes acceso al Radar de este proyecto");
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
}
