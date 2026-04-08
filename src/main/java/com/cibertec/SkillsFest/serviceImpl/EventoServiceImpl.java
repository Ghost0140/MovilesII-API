package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IEventoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements IEventoService {

    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ISedeRepository sedeRepository;

    @Override
    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll()
                .stream()
                .filter(e -> !"ELIMINADO".equals(e.getEstado()))
                .toList();
    }

    @Override
    public Optional<Evento> obtenerPorId(Long id) {
        return eventoRepository.findById(id)
                .filter(e -> !"ELIMINADO".equals(e.getEstado()));
    }

    @Override
    public List<Evento> obtenerEventosPublicados() {
        return eventoRepository.findByEstado("PUBLICADO");
    }

    @Override
    @Transactional
    public Evento crearEvento(Evento evento, Long creadorId, Long sedeId) {
        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        validarFechas(evento);

        evento.setCreadoPor(creador);
        evento.setSedeOrganizadora(sede);
        evento.setEstado("BORRADOR");

        return eventoRepository.save(evento);
    }

    @Override
    @Transactional
    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (eventoActualizado.getNombre() != null) evento.setNombre(eventoActualizado.getNombre());
        if (eventoActualizado.getDescripcion() != null) evento.setDescripcion(eventoActualizado.getDescripcion());
        if (eventoActualizado.getTipo() != null) evento.setTipo(eventoActualizado.getTipo());
        if (eventoActualizado.getAlcance() != null) evento.setAlcance(eventoActualizado.getAlcance());
        if (eventoActualizado.getFechaInicioInscripcion() != null) evento.setFechaInicioInscripcion(eventoActualizado.getFechaInicioInscripcion());
        if (eventoActualizado.getFechaFinInscripcion() != null) evento.setFechaFinInscripcion(eventoActualizado.getFechaFinInscripcion());
        if (eventoActualizado.getFechaEvento() != null) evento.setFechaEvento(eventoActualizado.getFechaEvento());
        if (eventoActualizado.getMaxMiembrosEquipo() != null) evento.setMaxMiembrosEquipo(eventoActualizado.getMaxMiembrosEquipo());

        validarFechas(evento);

        return eventoRepository.save(evento);
    }

    @Override
    @Transactional
    public Evento cambiarEstado(Long id, String estado) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setEstado(estado);
        return eventoRepository.save(evento);
    }

    @Override
    @Transactional
    public Evento eliminarLogico(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setEstado("ELIMINADO");
        return eventoRepository.save(evento);
    }

    private void validarFechas(Evento evento) {
        if (evento.getFechaInicioInscripcion() == null
                || evento.getFechaFinInscripcion() == null
                || evento.getFechaEvento() == null) {
            throw new RuntimeException("Las fechas del evento son obligatorias");
        }

        if (evento.getFechaFinInscripcion().isBefore(evento.getFechaInicioInscripcion())) {
            throw new RuntimeException("La fecha de cierre no puede ser menor a la fecha de inicio.");
        }

        if (evento.getFechaEvento().isBefore(evento.getFechaFinInscripcion())) {
            throw new RuntimeException("La fecha del evento no puede ser menor a la fecha de fin de inscripción.");
        }
    }
}