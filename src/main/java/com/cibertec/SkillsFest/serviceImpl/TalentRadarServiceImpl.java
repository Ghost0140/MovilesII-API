package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.dto.radar.RadarAnalysisResponse;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;

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
    public RadarAnalysisResponse analizarProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (proyecto.getRepositorioUrl() == null || proyecto.getRepositorioUrl().isBlank()) {
            throw new RuntimeException("El proyecto no tiene URL de repositorio");
        }

        Repositorio repositorio = repositorioRepository.findByProyectoId(proyectoId)
                .orElseGet(Repositorio::new);

        repositorio.setProyecto(proyecto);
        repositorio.setUrl(proyecto.getRepositorioUrl());
        repositorio.setPlataforma("GITHUB");
        repositorio.setEstadoAnalisis("EN_PROCESO");
        repositorio.setDetalleError(null);
        repositorio.setUltimoAnalisis(LocalDateTime.now());
        repositorio.setContributorsGithub(0);
        repositorio.setUsuariosMapeados(0);
        repositorio.setContribucionesGeneradas(0);
        repositorio = repositorioRepository.save(repositorio);

        List<String> advertencias = new ArrayList<>();

        try {
            String[] ownerRepo = extraerOwnerRepo(proyecto.getRepositorioUrl());
            String owner = ownerRepo[0];
            String repo = ownerRepo[1];

            Map<String, Double> lenguajes = obtenerLenguajes(owner, repo);
            List<Map<String, Object>> contributors = obtenerContribuidores(owner, repo);
            Set<String> pistasRepositorio = detectarPistasRepositorio(owner, repo);

            int totalCommits = contributors.stream()
                    .map(c -> toInt(c.getOrDefault("contributions", 0)))
                    .reduce(0, Integer::sum);

            Map<String, Double> scoreBase = calcularScoresBase(lenguajes, pistasRepositorio);

            int usuariosMapeados = 0;
            int contribucionesGeneradas = 0;

            for (Map<String, Object> contributor : contributors) {
                String login = String.valueOf(contributor.get("login"));
                int contributions = toInt(contributor.getOrDefault("contributions", 0));

                Optional<Usuario> usuarioOpt = usuarioRepository.findByGithubUsername(login);

                if (usuarioOpt.isEmpty()) {
                    String warning = "Contributor de GitHub no vinculado a usuario local: " + login;
                    advertencias.add(warning);
                    log.warn(warning);
                    continue;
                }

                Usuario usuario = usuarioOpt.get();
                usuariosMapeados++;

                Contribucion contribucion = contribucionRepository
                        .findByRepositorioIdAndUsuarioId(repositorio.getId(), usuario.getId())
                        .orElseGet(Contribucion::new);

                double factor = totalCommits > 0 ? ((double) contributions / totalCommits) : 0.0;
                contribucion.setRepositorio(repositorio);
                contribucion.setUsuario(usuario);
                contribucion.setTotalCommits(contributions);

                // IMPORTANTE:
                // Ya no inventamos líneas con factor * 10000.
                // Lo dejamos en null hasta implementar análisis real por archivos.
                contribucion.setTotalLineas(null);

                contribucion.setScoreFrontend(bd(scoreBase.get("FRONTEND") * factor));
                contribucion.setScoreBackend(bd(scoreBase.get("BACKEND") * factor));
                contribucion.setScoreBd(bd(scoreBase.get("BD") * factor));
                contribucion.setScoreMobile(bd(scoreBase.get("MOBILE") * factor));
                contribucion.setScoreTesting(bd(scoreBase.get("TESTING") * factor));
                contribucion.setTecnologiasDetectadas(generarTecnologiasDetectadas(lenguajes, pistasRepositorio));
                contribucion.setAnalizadoEn(LocalDateTime.now());

                contribucionRepository.save(contribucion);
                contribucionesGeneradas++;

                asegurarPortafolio(usuario);
                actualizarPortafolioRadar(usuario.getId());
            }

            repositorio.setTotalCommits(totalCommits);
            repositorio.setLenguajes(escribirJson(lenguajes));
            repositorio.setUltimoAnalisis(LocalDateTime.now());
            repositorio.setContributorsGithub(contributors.size());
            repositorio.setUsuariosMapeados(usuariosMapeados);
            repositorio.setContribucionesGeneradas(contribucionesGeneradas);

            if (contributors.isEmpty()) {
                repositorio.setEstadoAnalisis("INCOMPLETO");
                repositorio.setDetalleError("GitHub no devolvió contributors para este repositorio");
            } else if (usuariosMapeados == 0) {
                repositorio.setEstadoAnalisis("INCOMPLETO");
                repositorio.setDetalleError("No se pudo vincular ningún contributor con usuarios locales por githubUsername");
            } else if (contribucionesGeneradas == 0) {
                repositorio.setEstadoAnalisis("INCOMPLETO");
                repositorio.setDetalleError("No se generaron contribuciones");
            } else {
                repositorio.setEstadoAnalisis("COMPLETADO");
                repositorio.setDetalleError(null);
            }

            repositorio = repositorioRepository.save(repositorio);

            return RadarAnalysisResponse.builder()
                    .proyectoId(proyecto.getId())
                    .repositorioId(repositorio.getId())
                    .repositorioUrl(repositorio.getUrl())
                    .estado(repositorio.getEstadoAnalisis())
                    .totalCommits(totalCommits)
                    .contributorsGithub(contributors.size())
                    .usuariosMapeados(usuariosMapeados)
                    .contribucionesGeneradas(contribucionesGeneradas)
                    .lenguajesDetectados(lenguajes)
                    .advertencias(advertencias)
                    .mensaje(generarMensajeEstado(repositorio.getEstadoAnalisis()))
                    .build();

        } catch (Exception e) {
            repositorio.setEstadoAnalisis("ERROR");
            repositorio.setDetalleError(e.getMessage());
            repositorio.setUltimoAnalisis(LocalDateTime.now());
            repositorioRepository.save(repositorio);

            log.error("Error en análisis Talent Radar para proyecto {}: {}", proyectoId, e.getMessage());

            throw new RuntimeException("Error en análisis Talent Radar: " + e.getMessage());
        }
    }

    @Override
    public void generarRankingsPorArea(Long eventoId) {
        List<Proyecto> proyectos = proyectoRepository.findByEventoIdAndEstadoNot(eventoId, "ELIMINADO");

        if (proyectos.isEmpty()) {
            return;
        }

        List<Long> proyectoIds = proyectos.stream()
                .map(Proyecto::getId)
                .toList();

        List<Repositorio> repositorios = repositorioRepository.findByProyectoIdIn(proyectoIds);

        List<Contribucion> contribuciones = new ArrayList<>();

        for (Repositorio repo : repositorios) {
            contribuciones.addAll(contribucionRepository.findByRepositorioId(repo.getId()));
        }

        rankingAreaRepository.deleteAll(rankingAreaRepository.findByEventoId(eventoId));

        Evento evento = proyectos.get(0).getEvento();

        guardarRankingArea(evento, contribuciones, "FRONTEND");
        guardarRankingArea(evento, contribuciones, "BACKEND");
        guardarRankingArea(evento, contribuciones, "BD");
        guardarRankingArea(evento, contribuciones, "MOBILE");
        guardarRankingArea(evento, contribuciones, "TESTING");
    }

    @Override
    public void actualizarPortafolioRadar(Long usuarioId) {
        PortafolioPublico portfolio = portfolioRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseGet(() -> crearPortafolioBase(usuarioId));

        List<Contribucion> contribuciones = contribucionRepository.findByUsuarioId(usuarioId);

        if (contribuciones.isEmpty()) {
            return;
        }

        portfolio.setRadarFrontend(bd(promedio(contribuciones, "FRONTEND")));
        portfolio.setRadarBackend(bd(promedio(contribuciones, "BACKEND")));
        portfolio.setRadarBd(bd(promedio(contribuciones, "BD")));
        portfolio.setRadarMobile(bd(promedio(contribuciones, "MOBILE")));
        portfolio.setRadarTesting(bd(promedio(contribuciones, "TESTING")));
        portfolio.setActualizadoEn(LocalDateTime.now());

        portfolioRepository.save(portfolio);
    }

    @Override
    public List<RankingArea> obtenerRankingsPorArea(Long eventoId, String area) {
        return rankingAreaRepository.findByEventoIdAndArea(eventoId, area.toUpperCase());
    }

    private void asegurarPortafolio(Usuario usuario) {
        portfolioRepository.findByUsuarioIdAndActivoTrue(usuario.getId())
                .orElseGet(() -> crearPortafolioBase(usuario.getId()));
    }

    private PortafolioPublico crearPortafolioBase(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para crear portafolio"));

        PortafolioPublico nuevo = new PortafolioPublico();

        nuevo.setUsuario(usuario);
        nuevo.setVisible(true);
        nuevo.setActivo(true);
        nuevo.setSlug("user-" + usuario.getId());
        nuevo.setTitulo("Portafolio de " + obtenerNombreUsuario(usuario));
        nuevo.setBio("Portafolio generado automáticamente por Talent Radar");
        nuevo.setTotalEventos(0);
        nuevo.setTotalProyectos(0);
        nuevo.setPremiosObtenidos(0);
        nuevo.setRadarFrontend(BigDecimal.ZERO);
        nuevo.setRadarBackend(BigDecimal.ZERO);
        nuevo.setRadarBd(BigDecimal.ZERO);
        nuevo.setRadarMobile(BigDecimal.ZERO);
        nuevo.setRadarTesting(BigDecimal.ZERO);
        nuevo.setActualizadoEn(LocalDateTime.now());

        return portfolioRepository.save(nuevo);
    }

    private String obtenerNombreUsuario(Usuario usuario) {
        String nombres = usuario.getNombres() != null ? usuario.getNombres() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        String completo = (nombres + " " + apellidos).trim();

        if (!completo.isBlank()) {
            return completo;
        }

        if (usuario.getGithubUsername() != null && !usuario.getGithubUsername().isBlank()) {
            return usuario.getGithubUsername();
        }

        return "Usuario " + usuario.getId();
    }

    private void guardarRankingArea(Evento evento, List<Contribucion> contribuciones, String area) {
        Map<Long, Double> scorePorUsuario = new HashMap<>();

        for (Contribucion c : contribuciones) {
            double score = switch (area) {
                case "FRONTEND" -> valor(c.getScoreFrontend());
                case "BACKEND" -> valor(c.getScoreBackend());
                case "BD" -> valor(c.getScoreBd());
                case "MOBILE" -> valor(c.getScoreMobile());
                case "TESTING" -> valor(c.getScoreTesting());
                default -> 0.0;
            };

            scorePorUsuario.merge(c.getUsuario().getId(), score, Double::sum);
        }

        List<Map.Entry<Long, Double>> ordenados = scorePorUsuario.entrySet()
                .stream()
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

    private Map<String, Double> calcularScoresBase(Map<String, Double> lenguajes, Set<String> pistasRepositorio) {
        Map<String, Double> scores = new HashMap<>();

        double frontend = scorePorLenguajes(
                lenguajes,
                "javascript",
                "typescript",
                "html",
                "css",
                "react",
                "vue"
        );

        double backend = scorePorLenguajes(
                lenguajes,
                "java",
                "python",
                "c#",
                "node",
                "php",
                "go",
                "spring"
        );

        double bd = scorePorLenguajes(
                lenguajes,
                "sql",
                "plsql",
                "mysql",
                "postgres"
        );

        double mobile = scorePorLenguajes(
                lenguajes,
                "kotlin",
                "swift",
                "dart",
                "react native",
                "android"
        );

        double testing = scorePorLenguajes(
                lenguajes,
                "jest",
                "junit",
                "pytest",
                "cypress",
                "gherkin"
        );

        // ===========================
        // PISTAS POR ESTRUCTURA
        // ===========================

        if (pistasRepositorio.contains("react")) frontend += 20;
        if (pistasRepositorio.contains("vite")) frontend += 10;
        if (pistasRepositorio.contains("vue")) frontend += 15;
        if (pistasRepositorio.contains("angular")) frontend += 15;
        if (pistasRepositorio.contains("frontend-structure")) frontend += 10;

        if (pistasRepositorio.contains("spring")) backend += 20;
        if (pistasRepositorio.contains("maven")) backend += 10;
        if (pistasRepositorio.contains("gradle")) backend += 10;
        if (pistasRepositorio.contains("backend-structure")) backend += 10;

        if (pistasRepositorio.contains("sql-files")) bd += 25;
        if (pistasRepositorio.contains("migrations")) bd += 20;
        if (pistasRepositorio.contains("database-folder")) bd += 15;

        if (pistasRepositorio.contains("android")) mobile += 25;
        if (pistasRepositorio.contains("ios")) mobile += 25;
        if (pistasRepositorio.contains("flutter")) mobile += 20;
        if (pistasRepositorio.contains("react-native")) mobile += 20;

        if (pistasRepositorio.contains("tests")) testing += 25;
        if (pistasRepositorio.contains("jest")) testing += 20;
        if (pistasRepositorio.contains("cypress")) testing += 25;
        if (pistasRepositorio.contains("junit")) testing += 20;

        scores.put("FRONTEND", Math.min(frontend, 100.0));
        scores.put("BACKEND", Math.min(backend, 100.0));
        scores.put("BD", Math.min(bd, 100.0));
        scores.put("MOBILE", Math.min(mobile, 100.0));
        scores.put("TESTING", Math.min(testing, 100.0));

        return scores;
    }

    private double scorePorLenguajes(Map<String, Double> lenguajes, String... targets) {
        double total = 0.0;

        for (Map.Entry<String, Double> entry : lenguajes.entrySet()) {
            String lenguaje = entry.getKey().toLowerCase();

            for (String target : targets) {
                if (lenguaje.contains(target.toLowerCase())) {
                    total += entry.getValue();
                }
            }
        }

        return Math.min(total, 100.0);
    }

    private Double promedio(List<Contribucion> contribuciones, String area) {
        return contribuciones.stream()
                .mapToDouble(c -> switch (area) {
                    case "FRONTEND" -> valor(c.getScoreFrontend());
                    case "BACKEND" -> valor(c.getScoreBackend());
                    case "BD" -> valor(c.getScoreBd());
                    case "MOBILE" -> valor(c.getScoreMobile());
                    case "TESTING" -> valor(c.getScoreTesting());
                    default -> 0.0;
                })
                .average()
                .orElse(0.0);
    }

    private String generarTecnologiasDetectadas(Map<String, Double> lenguajes, Set<String> pistasRepositorio) {
        Map<String, Object> resultado = new HashMap<>();

        List<Map<String, Object>> lenguajesDetectados = lenguajes.entrySet()
                .stream()
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

        resultado.put("lenguajes", lenguajesDetectados);
        resultado.put("pistasRepositorio", pistasRepositorio);

        return escribirJson(resultado);
    }

    private Map<String, Double> obtenerLenguajes(String owner, String repo) {
        String body = githubGet("/repos/" + owner + "/" + repo + "/languages");

        try {
            Map<String, Integer> bytes = objectMapper.readValue(body, new TypeReference<>() {});
            long total = bytes.values()
                    .stream()
                    .mapToLong(Integer::longValue)
                    .sum();

            Map<String, Double> porcentajes = new HashMap<>();

            if (total == 0) {
                return porcentajes;
            }

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

    private Set<String> detectarPistasRepositorio(String owner, String repo) {
        Set<String> pistas = new HashSet<>();

        try {
            String body = githubGet("/repos/" + owner + "/" + repo + "/contents");
            JsonNode root = objectMapper.readTree(body);

            for (JsonNode node : root) {
                String name = node.path("name").asText("").toLowerCase();
                String type = node.path("type").asText("").toLowerCase();

                if ("package.json".equals(name)) {
                    pistas.add("node");
                }

                if ("vite.config.js".equals(name) || "vite.config.ts".equals(name)) {
                    pistas.add("vite");
                    pistas.add("frontend-structure");
                }

                if ("pom.xml".equals(name)) {
                    pistas.add("maven");
                    pistas.add("backend-structure");
                }

                if ("build.gradle".equals(name) || "build.gradle.kts".equals(name)) {
                    pistas.add("gradle");
                    pistas.add("backend-structure");
                    pistas.add("android");
                }

                if ("src".equals(name) && "dir".equals(type)) {
                    pistas.add("src");
                }

                if ("cypress".equals(name)) {
                    pistas.add("cypress");
                    pistas.add("tests");
                }

                if ("__tests__".equals(name) || "test".equals(name) || "tests".equals(name)) {
                    pistas.add("tests");
                }

                if ("database".equals(name) || "db".equals(name)) {
                    pistas.add("database-folder");
                }

                if ("migrations".equals(name)) {
                    pistas.add("migrations");
                }

                if ("android".equals(name)) {
                    pistas.add("android");
                }

                if ("ios".equals(name)) {
                    pistas.add("ios");
                }

                if (name.endsWith(".sql")) {
                    pistas.add("sql-files");
                }

                if ("pubspec.yaml".equals(name)) {
                    pistas.add("flutter");
                    pistas.add("mobile-structure");
                }

                if ("podfile".equals(name) || "info.plist".equals(name)) {
                    pistas.add("ios");
                }
            }

            if (pistas.contains("node")) {
                detectarPistasPackageJson(owner, repo, pistas);
            }

            if (pistas.contains("maven")) {
                detectarPistasPomXml(owner, repo, pistas);
            }

        } catch (Exception e) {
            log.warn("No se pudo analizar estructura del repositorio {}/{}: {}", owner, repo, e.getMessage());
        }

        return pistas;
    }

    private void detectarPistasPackageJson(String owner, String repo, Set<String> pistas) {
        try {
            String body = githubGet("/repos/" + owner + "/" + repo + "/contents/package.json");
            JsonNode json = objectMapper.readTree(body);

            String content = json.path("content").asText("");

            if (content == null || content.isBlank()) {
                return;
            }

            String decoded = new String(Base64.getMimeDecoder().decode(content));
            String lower = decoded.toLowerCase();

            if (lower.contains("\"react\"")) {
                pistas.add("react");
                pistas.add("frontend-structure");
            }

            if (lower.contains("\"vue\"")) {
                pistas.add("vue");
                pistas.add("frontend-structure");
            }

            if (lower.contains("\"@angular/core\"")) {
                pistas.add("angular");
                pistas.add("frontend-structure");
            }

            if (lower.contains("\"vite\"")) {
                pistas.add("vite");
                pistas.add("frontend-structure");
            }

            if (lower.contains("\"jest\"")) {
                pistas.add("jest");
                pistas.add("tests");
            }

            if (lower.contains("\"cypress\"")) {
                pistas.add("cypress");
                pistas.add("tests");
            }

            if (lower.contains("\"react-native\"")) {
                pistas.add("react-native");
                pistas.add("mobile-structure");
            }

            if (lower.contains("\"express\"") || lower.contains("\"nestjs\"")) {
                pistas.add("node-backend");
                pistas.add("backend-structure");
            }

        } catch (Exception e) {
            log.warn("No se pudo leer package.json para {}/{}: {}", owner, repo, e.getMessage());
        }
    }

    private void detectarPistasPomXml(String owner, String repo, Set<String> pistas) {
        try {
            String body = githubGet("/repos/" + owner + "/" + repo + "/contents/pom.xml");
            JsonNode json = objectMapper.readTree(body);

            String content = json.path("content").asText("");

            if (content == null || content.isBlank()) {
                return;
            }

            String decoded = new String(Base64.getMimeDecoder().decode(content));
            String lower = decoded.toLowerCase();

            if (lower.contains("spring-boot")) {
                pistas.add("spring");
                pistas.add("backend-structure");
            }

            if (lower.contains("mysql") || lower.contains("postgresql") || lower.contains("mariadb")) {
                pistas.add("database-folder");
            }

            if (lower.contains("junit") || lower.contains("mockito")) {
                pistas.add("junit");
                pistas.add("tests");
            }

        } catch (Exception e) {
            log.warn("No se pudo leer pom.xml para {}/{}: {}", owner, repo, e.getMessage());
        }
    }

    private String githubGet(String path) {
        try {
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

        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("GitHub API respondió "
                    + e.getStatusCode()
                    + ": "
                    + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error consumiendo GitHub API: " + e.getMessage());
        }
    }

    private String[] extraerOwnerRepo(String url) {
        try {
            String limpia = url
                    .replace(".git", "")
                    .trim();

            String[] partes = limpia.split("/");

            if (partes.length < 2) {
                throw new RuntimeException("URL inválida");
            }

            return new String[]{
                    partes[partes.length - 2],
                    partes[partes.length - 1]
            };

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

    private double valor(BigDecimal n) {
        return n != null ? n.doubleValue() : 0.0;
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Integer integer) {
            return integer;
        }

        if (value instanceof Long largo) {
            return largo.intValue();
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private String generarMensajeEstado(String estado) {
        return switch (estado) {
            case "COMPLETADO" -> "Análisis completado correctamente";
            case "INCOMPLETO" -> "Análisis incompleto. Revise contributors, usuarios vinculados o repositorio";
            case "ERROR" -> "Error durante el análisis";
            case "EN_PROCESO" -> "Análisis en proceso";
            case "PENDIENTE" -> "Análisis pendiente";
            default -> "Estado de análisis desconocido";
        };
    }
}