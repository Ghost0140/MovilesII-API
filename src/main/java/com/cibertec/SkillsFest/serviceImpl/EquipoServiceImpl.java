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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipoServiceImpl implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<Equipo> obtenerTodos() {
        return equipoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equals(e.getEstado()))
                .toList();
    }

    @Override
    public Optional<Equipo> obtenerPorId(Long id) {
        return equipoRepository.findById(id)
                .filter(e -> !"ELIMINADO".equals(e.getEstado()));
    }

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
            throw new RuntimeException("El equipo supera el límite máximo permitido.");
        }

        Usuario lider = usuarioRepository.findById(liderId)
                .orElseThrow(() -> new RuntimeException("Líder no encontrado"));

        String miembrosJson = "[]";
        try {
            if (miembrosIds != null && !miembrosIds.isEmpty()) {
                miembrosJson = objectMapper.writeValueAsString(miembrosIds);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar los miembros");
        }

        Usuario asesor = null;
        if (asesorId != null) {
            asesor = usuarioRepository.findById(asesorId)
                    .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));
        }

        Equipo equipo = new Equipo();
        equipo.setEvento(evento);
        equipo.setSede(lider.getSede());
        equipo.setNombre(nombreEquipo);
        equipo.setLider(lider);
        equipo.setMiembros(miembrosJson);
        equipo.setAsesor(asesor);
        equipo.setEstado("PENDIENTE");

        return equipoRepository.save(equipo);
    }

    @Override
    public List<Equipo> obtenerEquiposPorEvento(Long eventoId) {
        return equipoRepository.findByEventoIdAndEstadoNot(eventoId, "ELIMINADO");
    }

    @Override
    @Transactional
    public Equipo aprobarEquipo(Long equipoId, Long organizadorId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        equipo.setEstado("APROBADO");
        return equipoRepository.save(equipo);
    }

    @Override
    @Transactional
    public Equipo cambiarEstado(Long equipoId, String estado) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        equipo.setEstado(estado);
        return equipoRepository.save(equipo);
    }

    @Override
    @Transactional
    public Equipo eliminarLogico(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        equipo.setEstado("ELIMINADO");
        return equipoRepository.save(equipo);
    }
}