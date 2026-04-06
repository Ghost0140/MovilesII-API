package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IEquipoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipoServiceImpl implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Equipo inscribirEquipo(Long eventoId, Long liderId, List<Long> miembrosIds, Long asesorId, String nombreEquipo) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (!"PUBLICADO".equals(evento.getEstado())) {
            throw new RuntimeException("El evento no está abierto para inscripciones.");
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(evento.getFechaInicioInscripcion()) || hoy.isAfter(evento.getFechaFinInscripcion())) {
            throw new RuntimeException("La fecha actual está fuera del rango de inscripciones.");
        }

        int totalMiembros = 1 + (miembrosIds != null ? miembrosIds.size() : 0);
        if (evento.getMaxMiembrosEquipo() != null && totalMiembros > evento.getMaxMiembrosEquipo()) {
            throw new RuntimeException("El equipo supera el límite máximo permitido de " + evento.getMaxMiembrosEquipo() + " miembros.");
        }

        Usuario lider = usuarioRepository.findById(liderId)
                .orElseThrow(() -> new RuntimeException("Líder no encontrado"));

        if (lider.getRoles() == null || !lider.getRoles().contains("ESTUDIANTE")) {
            throw new RuntimeException("El líder del equipo debe ser un ESTUDIANTE.");
        }

        if ("SEDE".equals(evento.getAlcance())
                && !lider.getSede().getId().equals(evento.getSedeOrganizadora().getId())) {
            throw new RuntimeException("En eventos de SEDE, el líder debe pertenecer a la sede organizadora.");
        }

        String miembrosJson = "[]";
        try {
            if (miembrosIds != null && !miembrosIds.isEmpty()) {
                miembrosJson = objectMapper.writeValueAsString(miembrosIds);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar los miembros del equipo", e);
        }

        Usuario asesor = null;
        if (asesorId != null) {
            asesor = usuarioRepository.findById(asesorId)
                    .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));

            if (asesor.getRoles() == null || !asesor.getRoles().contains("PROFESOR")) {
                throw new RuntimeException("El asesor debe tener el rol de PROFESOR.");
            }
        }

        Equipo nuevoEquipo = new Equipo();
        nuevoEquipo.setEvento(evento);
        nuevoEquipo.setSede(lider.getSede());
        nuevoEquipo.setNombre(nombreEquipo);
        nuevoEquipo.setLider(lider);
        nuevoEquipo.setMiembros(miembrosJson);
        nuevoEquipo.setAsesor(asesor);
        nuevoEquipo.setAprobado(false);

        return equipoRepository.save(nuevoEquipo);
    }

    @Override
    public List<Equipo> obtenerEquiposPorEvento(Long eventoId) {
        return equipoRepository.findByEventoId(eventoId);
    }

    @Override
    @Transactional
    public Equipo aprobarEquipo(Long equipoId, Long organizadorId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Usuario organizador = usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        if (organizador.getRoles() == null ||
                (!organizador.getRoles().contains("ORGANIZADOR") && !organizador.getRoles().contains("PROFESOR"))) {
            throw new RuntimeException("No tienes permisos necesarios para aprobar equipos.");
        }

        equipo.setAprobado(true);
        return equipoRepository.save(equipo);
    }
}