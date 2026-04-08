package com.cibertec.SkillsFest.dto;

import com.cibertec.SkillsFest.entity.*;

public class ApiMapper {

    private ApiMapper() {
    }

    public static UsuarioResponse toUsuarioResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombres(),
                u.getApellidos(),
                u.getEmail(),
                u.getNumeroDocumento(),
                u.getCarrera(),
                u.getCiclo(),
                u.getCodigoEstudiante(),
                u.getGithubUsername(),
                u.getActivo(),
                u.getSede() != null ? u.getSede().getId() : null,
                u.getSede() != null ? u.getSede().getNombre() : null
        );
    }

    public static EventoResponse toEventoResponse(Evento e) {
        return new EventoResponse(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getTipo(),
                e.getAlcance(),
                e.getFechaInicioInscripcion(),
                e.getFechaFinInscripcion(),
                e.getFechaEvento(),
                e.getPermiteEquipos(),
                e.getMaxMiembrosEquipo(),
                e.getPermiteVotacionPopular(),
                e.getEstado(),
                e.getSedeOrganizadora() != null ? e.getSedeOrganizadora().getId() : null,
                e.getSedeOrganizadora() != null ? e.getSedeOrganizadora().getNombre() : null,
                e.getCreadoPor() != null ? e.getCreadoPor().getId() : null,
                e.getCreadoPor() != null ? e.getCreadoPor().getNombres() + " " + e.getCreadoPor().getApellidos() : null
        );
    }

    public static EquipoResponse toEquipoResponse(Equipo e) {
        return new EquipoResponse(
                e.getId(),
                e.getNombre(),
                e.getEstado(),
                e.getEvento() != null ? e.getEvento().getId() : null,
                e.getEvento() != null ? e.getEvento().getNombre() : null,
                e.getSede() != null ? e.getSede().getId() : null,
                e.getSede() != null ? e.getSede().getNombre() : null,
                e.getLider() != null ? e.getLider().getId() : null,
                e.getLider() != null ? e.getLider().getNombres() + " " + e.getLider().getApellidos() : null,
                e.getAsesor() != null ? e.getAsesor().getId() : null,
                e.getAsesor() != null ? e.getAsesor().getNombres() + " " + e.getAsesor().getApellidos() : null,
                e.getMiembros()
        );
    }

    public static ProyectoResponse toProyectoResponse(Proyecto p) {
        return new ProyectoResponse(
                p.getId(),
                p.getTitulo(),
                p.getResumen(),
                p.getDescripcion(),
                p.getRepositorioUrl(),
                p.getVideoUrl(),
                p.getDemoUrl(),
                p.getTecnologias(),
                p.getEstado(),
                p.getEvento() != null ? p.getEvento().getId() : null,
                p.getEvento() != null ? p.getEvento().getNombre() : null,
                p.getEquipo() != null ? p.getEquipo().getId() : null,
                p.getEquipo() != null ? p.getEquipo().getNombre() : null,
                p.getUsuario() != null ? p.getUsuario().getId() : null,
                p.getUsuario() != null ? p.getUsuario().getNombres() + " " + p.getUsuario().getApellidos() : null
        );
    }

    public static EvaluacionResponse toEvaluacionResponse(Evaluacion e) {
        return new EvaluacionResponse(
                e.getId(),
                e.getProyecto() != null ? e.getProyecto().getId() : null,
                e.getProyecto() != null ? e.getProyecto().getTitulo() : null,
                e.getJurado() != null ? e.getJurado().getId() : null,
                e.getJurado() != null ? e.getJurado().getNombres() + " " + e.getJurado().getApellidos() : null,
                e.getCriterio() != null ? e.getCriterio().getId() : null,
                e.getCriterio() != null ? e.getCriterio().getNombre() : null,
                e.getPuntaje(),
                e.getComentario(),
                e.getEvaluadoEn()
        );
    }

    public static PortafolioResponse toPortafolioResponse(PortafolioPublico p) {
        return new PortafolioResponse(
                p.getId(),
                p.getUsuario() != null ? p.getUsuario().getId() : null,
                p.getUsuario() != null ? p.getUsuario().getNombres() + " " + p.getUsuario().getApellidos() : null,
                p.getVisible(),
                p.getActivo(),
                p.getSlug(),
                p.getTitulo(),
                p.getBio(),
                p.getTotalEventos(),
                p.getTotalProyectos(),
                p.getPremiosObtenidos(),
                p.getRadarFrontend(),
                p.getRadarBackend(),
                p.getRadarBd(),
                p.getRadarMobile(),
                p.getRadarTesting()
        );
    }

    public static ContribucionResponse toContribucionResponse(Contribucion c) {
        return new ContribucionResponse(
                c.getId(),
                c.getRepositorio() != null ? c.getRepositorio().getId() : null,
                c.getRepositorio() != null ? c.getRepositorio().getUrl() : null,
                c.getUsuario() != null ? c.getUsuario().getId() : null,
                c.getUsuario() != null ? c.getUsuario().getNombres() + " " + c.getUsuario().getApellidos() : null,
                c.getTotalCommits(),
                c.getTotalLineas(),
                c.getScoreFrontend(),
                c.getScoreBackend(),
                c.getScoreBd(),
                c.getScoreMobile(),
                c.getScoreTesting(),
                c.getTecnologiasDetectadas(),
                c.getAnalizadoEn()
        );
    }

    public static RepositorioResponse toRepositorioResponse(Repositorio r) {
        return new RepositorioResponse(
                r.getId(),
                r.getProyecto() != null ? r.getProyecto().getId() : null,
                r.getProyecto() != null ? r.getProyecto().getTitulo() : null,
                r.getUrl(),
                r.getPlataforma(),
                r.getTotalCommits(),
                r.getLenguajes(),
                r.getUltimoAnalisis()
        );
    }

    public static RankingAreaResponse toRankingAreaResponse(RankingArea r) {
        return new RankingAreaResponse(
                r.getId(),
                r.getEvento() != null ? r.getEvento().getId() : null,
                r.getEvento() != null ? r.getEvento().getNombre() : null,
                r.getUsuario() != null ? r.getUsuario().getId() : null,
                r.getUsuario() != null ? r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos() : null,
                r.getArea(),
                r.getScore(),
                r.getPosicion()
        );
    }

    public static RankingSedeResponse toRankingSedeResponse(RankingSede r) {
        return new RankingSedeResponse(
                r.getId(),
                r.getEvento() != null ? r.getEvento().getId() : null,
                r.getEvento() != null ? r.getEvento().getNombre() : null,
                r.getSede() != null ? r.getSede().getId() : null,
                r.getSede() != null ? r.getSede().getNombre() : null,
                r.getPosicion(),
                r.getPuntosTotales(),
                r.getProyectosPresentados()
        );
    }

    public static ResultadoResponse toResultadoResponse(Resultado r) {
        return new ResultadoResponse(
                r.getId(),
                r.getEvento() != null ? r.getEvento().getId() : null,
                r.getEvento() != null ? r.getEvento().getNombre() : null,
                r.getProyecto() != null ? r.getProyecto().getId() : null,
                r.getProyecto() != null ? r.getProyecto().getTitulo() : null,
                r.getPuntajeJurados(),
                r.getPuntajePopular(),
                r.getPuntajeTotal(),
                r.getPosicion(),
                r.getCategoriaPremio(),
                r.getEstado()
        );
    }
}