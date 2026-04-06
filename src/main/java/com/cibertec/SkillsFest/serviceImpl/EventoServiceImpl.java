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

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements IEventoService {

    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ISedeRepository sedeRepository;

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

        if (evento.getFechaFinInscripcion().isBefore(evento.getFechaInicioInscripcion())) {
            throw new RuntimeException("La fecha de cierre no puede ser menor a la fecha de inicio.");
        }

        if (evento.getFechaEvento().isBefore(evento.getFechaFinInscripcion())) {
            throw new RuntimeException("La fecha del evento no puede ser menor a la fecha de fin de inscripción.");
        }

        evento.setCreadoPor(creador);
        evento.setSedeOrganizadora(sede);
        evento.setEstado("BORRADOR");

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
}