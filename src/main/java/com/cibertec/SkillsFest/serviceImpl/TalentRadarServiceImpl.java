package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.*;
import com.cibertec.SkillsFest.repository.*;
import com.cibertec.SkillsFest.service.ITalentRadarService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TalentRadarServiceImpl implements ITalentRadarService {

    private final IProyectoRepository proyectoRepository;
    private final IRepositorioRepository repositorioRepository;
    private final IContribucionRepository contribucionRepository;
    private final IRankingAreaRepository rankingAreaRepository;
    private final IPortafolioPublicoRepository portfolioRepository;
    private final IUsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${github.api.token:}")
    private String githubToken;

    @Value("${github.api.baseurl:https://api.github.com}")
    private String githubBaseUrl;

    @Override
    public void analizarProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (proyecto.getRepositorioUrl() == null || proyecto.getRepositorioUrl().isBlank()) {
            throw new RuntimeException("El proyecto no tiene URL de repositorio");
        }

        String[] ownerRepo = extraerOwnerRepo(proyecto.getRepositorioUrl());
        String owner = ownerRepo[0];
        String repo = ownerRepo[1];

        Map<String, Double> lenguajes = obtenerLenguajes(owner, repo);
        List<Map<String, Object>> contributors = obtenerContribuidores(owner, repo);

        int totalCommits = contributors.stream()
                .map(c -> (Integer) c.getOrDefault("contributions", 0))
                .reduce(0, Integer::sum);

        Repositorio repositorio = repositorioRepository.findByProyectoId(proyectoId)
                .orElseGet(Repositorio::new);

        repositorio.setProyecto(proyecto);
        repositorio.setUrl(proyecto.getRepositorioUrl());
        repositorio.setPlataforma("GITHUB");
        repositorio.setTotalCommits(totalCommits);
        repositorio.setLenguajes(escribirJson(lenguajes));
        repositorio.setUltimoAnalisis(LocalDateTime.now());

        repositorio = repositorioRepository.save(repositorio);

        Map<String, Double> scoreBase = calcularScoresBase(lenguajes);

        for (Map<String, Object> contributor : contributors) {
            String login = (String) contributor.get("login");
            Integer contributions = (Integer) contributor.getOrDefault("contributions", 0);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByGithubUsername(login);
            if (usuarioOpt.isEmpty()) {
                log.warn("No se encontró usuario local para githubUsername={}", login);
                continue;
            }

            Usuario usuario = usuarioOpt.get();

            Contribucion contribucion = contribucionRepository
                    .findByRepositorioIdAndUsuarioId(repositorio.getId(), usuario.getId())
                    .orElse(new Contribucion());

            double factor = totalCommits > 0 ? (contributions.doubleValue() / totalCommits) : 0.0;

            contribucion.setRepositorio(repositorio);
            contribucion.setUsuario(usuario);
            contribucion.setTotalCommits(contributions);
            contribucion.setTotalLineas((int) Math.round(factor * 10000));
            contribucion.setScoreFrontend(bd(scoreBase.get("FRONTEND") * factor));
            contribucion.setScoreBackend(bd(scoreBase.get("BACKEND") * factor));
            contribucion.setScoreBd(bd(scoreBase.get("BD") * factor));
            contribucion.setScoreMobile(bd(scoreBase.get("MOBILE") * factor));
            contribucion.setScoreTesting(bd(scoreBase.get("TESTING") * factor));
            contribucion.setTecnologiasDetectadas(generarTecnologiasDetectadas(lenguajes));
            contribucion.setAnalizadoEn(LocalDateTime.now());

            contribucionRepository.save(contribucion);
            actualizarPortafolioRadar(usuario.getId());
        }
    }

    @Override
    public void generarRankingsPorArea(Long eventoId) {
        List<Proyecto> proyectos = proyectoRepository.findByEventoIdAndEstadoNot(eventoId, "ELIMINADO");
        if (proyectos.isEmpty()) return;

        List<Long> proyectoIds = proyectos.stream().map(Proyecto::getId).toList();
        List<Repositorio> repositorios = repositorioRepository.findByProyectoIdIn(proyectoIds);

        List<Contribucion> contribuciones = new ArrayList<>();
        for (Repositorio repo : repositorios) {
            contribuciones.addAll(contribucionRepository.findByRepositorioId(repo.getId()));
        }

        rankingAreaRepository.deleteAll(rankingAreaRepository.findByEventoId(eventoId));

        guardarRankingArea(proyectos.get(0).getEvento(), contribuciones, "FRONTEND");
        guardarRankingArea(proyectos.get(0).getEvento(), contribuciones, "BACKEND");
        guardarRankingArea(proyectos.get(0).getEvento(), contribuciones, "BD");
        guardarRankingArea(proyectos.get(0).getEvento(), contribuciones, "MOBILE");
        guardarRankingArea(proyectos.get(0).getEvento(), contribuciones, "TESTING");
    }

    @Override
    public void actualizarPortafolioRadar(Long usuarioId) {
        PortafolioPublico portfolio = portfolioRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        List<Contribucion> contribuciones = contribucionRepository.findByUsuarioId(usuarioId);
        if (contribuciones.isEmpty()) return;

        portfolio.setRadarFrontend(bd(promedio(contribuciones, "FRONTEND")));
        portfolio.setRadarBackend(bd(promedio(contribuciones, "BACKEND")));
        portfolio.setRadarBd(bd(promedio(contribuciones, "BD")));
        portfolio.setRadarMobile(bd(promedio(contribuciones, "MOBILE")));
        portfolio.setRadarTesting(bd(promedio(contribuciones, "TESTING")));

        portfolioRepository.save(portfolio);
    }

    @Override
    public List<RankingArea> obtenerRankingsPorArea(Long eventoId, String area) {
        return rankingAreaRepository.findByEventoIdAndArea(eventoId, area.toUpperCase());
    }

    private void guardarRankingArea(Evento evento, List<Contribucion> contribuciones, String area) {
        Map<Long, Double> scorePorUsuario = new HashMap<>();

        for (Contribucion c : contribuciones) {
            Double score = switch (area) {
                case "FRONTEND" -> c.getScoreFrontend() != null ? c.getScoreFrontend().doubleValue() : 0.0;
                case "BACKEND" -> c.getScoreBackend() != null ? c.getScoreBackend().doubleValue() : 0.0;
                case "BD" -> c.getScoreBd() != null ? c.getScoreBd().doubleValue() : 0.0;
                case "MOBILE" -> c.getScoreMobile() != null ? c.getScoreMobile().doubleValue() : 0.0;
                case "TESTING" -> c.getScoreTesting() != null ? c.getScoreTesting().doubleValue() : 0.0;
                default -> 0.0;
            };

            scorePorUsuario.merge(c.getUsuario().getId(), score, Double::sum);
        }

        List<Map.Entry<Long, Double>> ordenados = scorePorUsuario.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();

        int posicion = 1;
        for (Map.Entry<Long, Double> entry : ordenados) {
            Usuario usuario = usuarioRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            RankingArea ranking = new RankingArea();
            ranking.setEvento(evento);
            ranking.setUsuario(usuario);
            ranking.setArea(area);
            ranking.setScore(bd(entry.getValue()));
            ranking.setPosicion(posicion++);

            rankingAreaRepository.save(ranking);
        }
    }

    private Map<String, Double> calcularScoresBase(Map<String, Double> lenguajes) {
        Map<String, Double> scores = new HashMap<>();
        scores.put("FRONTEND", scorePorLenguajes(lenguajes, "javascript", "typescript", "html", "css", "react", "vue"));
        scores.put("BACKEND", scorePorLenguajes(lenguajes, "java", "python", "c#", "node", "php", "go", "spring"));
        scores.put("BD", scorePorLenguajes(lenguajes, "sql", "plsql", "mysql", "postgres"));
        scores.put("MOBILE", scorePorLenguajes(lenguajes, "kotlin", "swift", "dart", "react native", "android"));
        scores.put("TESTING", scorePorLenguajes(lenguajes, "jest", "junit", "pytest", "cypress"));
        return scores;
    }

    private double scorePorLenguajes(Map<String, Double> lenguajes, String... targets) {
        double total = 0.0;
        for (Map.Entry<String, Double> entry : lenguajes.entrySet()) {
            for (String target : targets) {
                if (entry.getKey().toLowerCase().contains(target.toLowerCase())) {
                    total += entry.getValue();
                }
            }
        }
        return Math.min(total, 100.0);
    }

    private Double promedio(List<Contribucion> contribuciones, String area) {
        return contribuciones.stream()
                .mapToDouble(c -> switch (area) {
                    case "FRONTEND" -> c.getScoreFrontend() != null ? c.getScoreFrontend().doubleValue() : 0.0;
                    case "BACKEND" -> c.getScoreBackend() != null ? c.getScoreBackend().doubleValue() : 0.0;
                    case "BD" -> c.getScoreBd() != null ? c.getScoreBd().doubleValue() : 0.0;
                    case "MOBILE" -> c.getScoreMobile() != null ? c.getScoreMobile().doubleValue() : 0.0;
                    case "TESTING" -> c.getScoreTesting() != null ? c.getScoreTesting().doubleValue() : 0.0;
                    default -> 0.0;
                })
                .average()
                .orElse(0.0);
    }

    private String generarTecnologiasDetectadas(Map<String, Double> lenguajes) {
        List<Map<String, Object>> data = lenguajes.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", e.getKey());
                    item.put("nivel", e.getValue() >= 40 ? "AVANZADO" : e.getValue() >= 20 ? "INTERMEDIO" : "BASICO");
                    item.put("porcentaje", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        return escribirJson(data);
    }

    private Map<String, Double> obtenerLenguajes(String owner, String repo) {
        String body = githubGet("/repos/" + owner + "/" + repo + "/languages");

        try {
            Map<String, Integer> bytes = objectMapper.readValue(body, new TypeReference<>() {});
            long total = bytes.values().stream().mapToLong(Integer::longValue).sum();

            Map<String, Double> porcentajes = new HashMap<>();
            if (total == 0) return porcentajes;

            for (Map.Entry<String, Integer> e : bytes.entrySet()) {
                porcentajes.put(e.getKey(), redondear((e.getValue() * 100.0) / total));
            }
            return porcentajes;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo lenguajes de GitHub: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> obtenerContribuidores(String owner, String repo) {
        String body = githubGet("/repos/" + owner + "/" + repo + "/contributors?per_page=100");

        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo contribuidores de GitHub: " + e.getMessage());
        }
    }

    private String githubGet(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (githubToken != null && !githubToken.isBlank() && !githubToken.contains("XXXXXXXXXXXXXXXX")) {
            headers.setBearerAuth(githubToken);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                githubBaseUrl + path,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    private String[] extraerOwnerRepo(String url) {
        try {
            String limpia = url.replace(".git", "");
            String[] partes = limpia.split("/");
            return new String[]{partes[partes.length - 2], partes[partes.length - 1]};
        } catch (Exception e) {
            throw new RuntimeException("No se pudo parsear la URL del repositorio: " + url);
        }
    }

    private String escribirJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo a JSON", e);
        }
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private BigDecimal bd(double valor) {
        return BigDecimal.valueOf(Math.round(valor * 100.0) / 100.0);
    }
}