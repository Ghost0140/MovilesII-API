package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.AppPortafolioResponse;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IPortafolioPublicoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/app/portafolios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppPortafolioController {

    private final IPortafolioPublicoRepository portafolioRepository;
    private final IUsuarioRepository usuarioRepository;

    @GetMapping("/publicos")
    public ResponseEntity<List<AppPortafolioResponse>> listarPortafoliosPublicos(
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<AppPortafolioResponse> response = portafolioRepository.findAll()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                .sorted((a, b) -> compararPorArea(b, a, area))
                .limit(limit)
                .map(this::mapPortafolio)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<AppPortafolioResponse> obtenerPortafolioPublico(@PathVariable Long usuarioId) {
        PortafolioPublico portafolio = portafolioRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        if (!Boolean.TRUE.equals(portafolio.getVisible())) {
            throw new RuntimeException("Este portafolio no es público");
        }

        return ResponseEntity.ok(mapPortafolio(portafolio));
    }

    @GetMapping("/mi-portafolio")
    public ResponseEntity<AppPortafolioResponse> obtenerMiPortafolio(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        PortafolioPublico portafolio = portafolioRepository.findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Todavía no tienes portafolio público"));

        return ResponseEntity.ok(mapPortafolio(portafolio));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private AppPortafolioResponse mapPortafolio(PortafolioPublico p) {
        Usuario u = p.getUsuario();

        return AppPortafolioResponse.builder()
                .portafolioId(p.getId())

                .usuarioId(u != null ? u.getId() : null)
                .nombres(u != null ? u.getNombres() : null)
                .apellidos(u != null ? u.getApellidos() : null)
                .email(u != null ? u.getEmail() : null)
                .githubUsername(u != null ? u.getGithubUsername() : null)
                .sede(u != null && u.getSede() != null ? u.getSede().getNombre() : null)
                .carrera(u != null ? u.getCarrera() : null)
                .ciclo(u != null ? u.getCiclo() : null)

                .visible(p.getVisible())
                .activo(p.getActivo())
                .slug(p.getSlug())
                .titulo(p.getTitulo())
                .bio(p.getBio())

                .totalEventos(p.getTotalEventos())
                .totalProyectos(p.getTotalProyectos())
                .premiosObtenidos(p.getPremiosObtenidos())

                .radarFrontend(valorOrZero(p.getRadarFrontend()))
                .radarBackend(valorOrZero(p.getRadarBackend()))
                .radarBd(valorOrZero(p.getRadarBd()))
                .radarMobile(valorOrZero(p.getRadarMobile()))
                .radarTesting(valorOrZero(p.getRadarTesting()))

                .actualizadoEn(p.getActualizadoEn())
                .build();
    }

    private int compararPorArea(PortafolioPublico a, PortafolioPublico b, String area) {
        BigDecimal scoreA = obtenerScore(a, area);
        BigDecimal scoreB = obtenerScore(b, area);

        return scoreA.compareTo(scoreB);
    }

    private BigDecimal obtenerScore(PortafolioPublico p, String area) {
        if (area == null || area.isBlank()) {
            return promedioGeneral(p);
        }

        return switch (area.toUpperCase()) {
            case "FRONTEND" -> valorOrZero(p.getRadarFrontend());
            case "BACKEND" -> valorOrZero(p.getRadarBackend());
            case "BD" -> valorOrZero(p.getRadarBd());
            case "MOBILE" -> valorOrZero(p.getRadarMobile());
            case "TESTING" -> valorOrZero(p.getRadarTesting());
            default -> promedioGeneral(p);
        };
    }

    private BigDecimal promedioGeneral(PortafolioPublico p) {
        BigDecimal total = valorOrZero(p.getRadarFrontend())
                .add(valorOrZero(p.getRadarBackend()))
                .add(valorOrZero(p.getRadarBd()))
                .add(valorOrZero(p.getRadarMobile()))
                .add(valorOrZero(p.getRadarTesting()));

        return total.divide(BigDecimal.valueOf(5), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal valorOrZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}