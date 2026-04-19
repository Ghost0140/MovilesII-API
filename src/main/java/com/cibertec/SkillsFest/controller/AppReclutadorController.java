package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppContribuidorResponse;
import com.cibertec.SkillsFest.dto.app.AppProyectoDestacadoResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IRepositorioRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/app/reclutador")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppReclutadorController {

    private final IContribucionRepository contribucionRepository;
    private final IRepositorioRepository repositorioRepository;
    private final IProyectoRepository proyectoRepository;
    private final IUsuarioRepository usuarioRepository;

    @GetMapping("/mejores-contribuidores")
    public ResponseEntity<List<AppContribuidorResponse>> listarMejoresContribuidores(
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication
    ) {
        validarRolReclutador(authentication);

        List<AppContribuidorResponse> response = contribucionRepository.findAll()
                .stream()
                .sorted((a, b) -> compararPorArea(b, a, area))
                .limit(limit)
                .map(this::mapContribuidor)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/proyectos-destacados")
    public ResponseEntity<List<AppProyectoDestacadoResponse>> listarProyectosDestacados(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication
    ) {
        validarRolReclutador(authentication);

        List<AppProyectoDestacadoResponse> response = proyectoRepository.findAll()
                .stream()
                .filter(p -> !"ELIMINADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstado()) || "ENVIADO".equalsIgnoreCase(p.getEstado()))
                .map(this::mapProyectoDestacado)
                .sorted((a, b) -> compararBigDecimal(b.getScorePromedioProyecto(), a.getScorePromedioProyecto()))
                .limit(limit)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/contribuidor/{usuarioId}")
    public ResponseEntity<List<AppContribuidorResponse>> obtenerHistorialContribuidor(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        validarRolReclutador(authentication);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<AppContribuidorResponse> response = contribucionRepository.findByUsuarioId(usuario.getId())
                .stream()
                .sorted(Comparator.comparing(Contribucion::getAnalizadoEn).reversed())
                .map(this::mapContribuidor)
                .toList();

        return ResponseEntity.ok(response);
    }

    private void validarRolReclutador(Authentication authentication) {
        boolean autorizado = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN") ||
                                a.getAuthority().equals("ROLE_ORGANIZADOR") ||
                                a.getAuthority().equals("ROLE_PROFESOR") ||
                                a.getAuthority().equals("ROLE_RECLUTADOR")
                );

        if (!autorizado) {
            throw new RuntimeException("No tienes permisos para acceder a esta sección");
        }
    }

    private AppContribuidorResponse mapContribuidor(Contribucion c) {
        Usuario u = c.getUsuario();
        Repositorio r = c.getRepositorio();
        Proyecto p = r != null ? r.getProyecto() : null;

        return AppContribuidorResponse.builder()
                .usuarioId(u != null ? u.getId() : null)
                .nombres(u != null ? u.getNombres() : null)
                .apellidos(u != null ? u.getApellidos() : null)
                .email(u != null ? u.getEmail() : null)
                .githubUsername(u != null ? u.getGithubUsername() : null)
                .sede(u != null && u.getSede() != null ? u.getSede().getNombre() : null)
                .carrera(u != null ? u.getCarrera() : null)
                .ciclo(u != null ? u.getCiclo() : null)

                .contribucionId(c.getId())
                .repositorioId(r != null ? r.getId() : null)
                .repositorioUrl(r != null ? r.getUrl() : null)

                .proyectoId(p != null ? p.getId() : null)
                .proyectoTitulo(p != null ? p.getTitulo() : null)
                .proyectoResumen(p != null ? p.getResumen() : null)
                .tipoParticipacion(p != null && p.getEquipo() != null ? "EQUIPO" : "INDIVIDUAL")

                .eventoId(p != null && p.getEvento() != null ? p.getEvento().getId() : null)
                .eventoNombre(p != null && p.getEvento() != null ? p.getEvento().getNombre() : null)

                .totalCommits(c.getTotalCommits())
                .totalLineas(c.getTotalLineas())

                .scoreFrontend(valorOrZero(c.getScoreFrontend()))
                .scoreBackend(valorOrZero(c.getScoreBackend()))
                .scoreBd(valorOrZero(c.getScoreBd()))
                .scoreMobile(valorOrZero(c.getScoreMobile()))
                .scoreTesting(valorOrZero(c.getScoreTesting()))
                .scorePromedio(calcularPromedio(c))

                .tecnologiasDetectadas(c.getTecnologiasDetectadas())
                .analizadoEn(c.getAnalizadoEn())
                .build();
    }

    private AppProyectoDestacadoResponse mapProyectoDestacado(Proyecto p) {
        Repositorio repo = repositorioRepository.findByProyectoId(p.getId()).orElse(null);

        BigDecimal promedioProyecto = BigDecimal.ZERO;

        if (repo != null) {
            List<Contribucion> contribuciones = contribucionRepository.findByRepositorioId(repo.getId());

            promedioProyecto = contribuciones.stream()
                    .map(this::calcularPromedio)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (!contribuciones.isEmpty()) {
                promedioProyecto = promedioProyecto.divide(
                        BigDecimal.valueOf(contribuciones.size()),
                        2,
                        java.math.RoundingMode.HALF_UP
                );
            }
        }

        return AppProyectoDestacadoResponse.builder()
                .proyectoId(p.getId())
                .titulo(p.getTitulo())
                .resumen(p.getResumen())
                .descripcion(p.getDescripcion())
                .estado(p.getEstado())
                .repositorioUrl(p.getRepositorioUrl())
                .tipoParticipacion(p.getEquipo() != null ? "EQUIPO" : "INDIVIDUAL")

                .eventoId(p.getEvento() != null ? p.getEvento().getId() : null)
                .eventoNombre(p.getEvento() != null ? p.getEvento().getNombre() : null)

                .equipoId(p.getEquipo() != null ? p.getEquipo().getId() : null)
                .equipoNombre(p.getEquipo() != null ? p.getEquipo().getNombre() : null)

                .totalCommits(repo != null ? repo.getTotalCommits() : 0)
                .contributorsGithub(repo != null ? repo.getContributorsGithub() : 0)
                .usuariosMapeados(repo != null ? repo.getUsuariosMapeados() : 0)
                .contribucionesGeneradas(repo != null ? repo.getContribucionesGeneradas() : 0)
                .estadoRadar(repo != null ? repo.getEstadoAnalisis() : "PENDIENTE")

                .scorePromedioProyecto(promedioProyecto)
                .build();
    }

    private int compararPorArea(Contribucion a, Contribucion b, String area) {
        BigDecimal scoreA = obtenerScorePorArea(a, area);
        BigDecimal scoreB = obtenerScorePorArea(b, area);
        return compararBigDecimal(scoreA, scoreB);
    }

    private BigDecimal obtenerScorePorArea(Contribucion c, String area) {
        if (area == null || area.isBlank()) {
            return calcularPromedio(c);
        }

        return switch (area.toUpperCase()) {
            case "FRONTEND" -> valorOrZero(c.getScoreFrontend());
            case "BACKEND" -> valorOrZero(c.getScoreBackend());
            case "BD" -> valorOrZero(c.getScoreBd());
            case "MOBILE" -> valorOrZero(c.getScoreMobile());
            case "TESTING" -> valorOrZero(c.getScoreTesting());
            default -> calcularPromedio(c);
        };
    }

    private BigDecimal calcularPromedio(Contribucion c) {
        BigDecimal total = valorOrZero(c.getScoreFrontend())
                .add(valorOrZero(c.getScoreBackend()))
                .add(valorOrZero(c.getScoreBd()))
                .add(valorOrZero(c.getScoreMobile()))
                .add(valorOrZero(c.getScoreTesting()));

        return total.divide(BigDecimal.valueOf(5), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal valorOrZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private int compararBigDecimal(BigDecimal a, BigDecimal b) {
        return a.compareTo(b);
    }
}