package com.cibertec.SkillsFest.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IEventoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements IEventoService{

	private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ISedeRepository sedeRepository; // Usamos tu interfaz con la 'I'

    @Override
    public List<Evento> obtenerEventosPublicados() {
        return eventoRepository.findByEstado("PUBLICADO");
    }

    @Override
    @Transactional
    public Evento crearEvento(Evento evento, Long creadorId, Long sedeId) {
        // 1. Validar al creador
        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        if (!creador.getRoles().contains("ORGANIZADOR") && !creador.getRoles().contains("ADMIN")) {
            throw new RuntimeException("No tienes permisos. Solo los organizadores pueden crear eventos.");
        }

        // 2. Validar la Sede
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        // 3. Reglas de Negocio: Validar lógica de fechas
        if (evento.getFechaFinInscripcion().isBefore(evento.getFechaInicioInscripcion())) {
            throw new RuntimeException("La fecha de cierre de inscripciones no puede ser menor a la fecha de inicio.");
        }
        if (evento.getFechaEvento().isBefore(evento.getFechaFinInscripcion())) {
            throw new RuntimeException("El día del evento no puede ocurrir antes de que cierren las inscripciones.");
        }

        // 4. Conectar relaciones y forzar estado inicial
        evento.setCreadoPor(creador);
        evento.setSedeOrganizadora(sede);
        evento.setEstado("BORRADOR"); // Todo evento nace oculto hasta que el profe lo publique

        return eventoRepository.save(evento);
    }
}
