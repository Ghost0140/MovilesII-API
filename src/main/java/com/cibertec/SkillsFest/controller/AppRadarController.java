package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.app.MiRadarResponse;
import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
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
@RequestMapping("/api/app")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppRadarController {

    private final IUsuarioRepository usuarioRepository;
    private final IContribucionRepository contribucionRepository;
    private final IPortafolioPublicoRepository portafolioRepository;

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
}