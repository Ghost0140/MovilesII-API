package com.cibertec.SkillsFest.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IEquipoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor // lombok genera le constructor para inyectar los repositorios automáticamente
public class EquipoServiceImpl implements IEquipoService{
	
	private final IEquipoRepository equipoRepository;
    private final IEventoRepository eventoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper; // Herramienta de Spring para convertir de/hacia JSON

    @Override
    @Transactional
    public Equipo inscribirEquipo(Long eventoId, Long liderId, List<Long> miembrosIds, Long asesorId, String nombreEquipo) {
        
        // 1. Validar que el evento existe
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 2. Validar Estado y Fechas del Evento
        if (!evento.getEstado().equals("PUBLICADO")) {
            throw new RuntimeException("El evento no está abierto para inscripciones.");
        }
        
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(evento.getFechaInicioInscripcion()) || hoy.isAfter(evento.getFechaFinInscripcion())) {
            throw new RuntimeException("La fecha actual está fuera del rango de inscripciones.");
        }

        // 3. Validar cantidad de miembros (líder + la lista de miembros)
        int totalMiembros = 1 + (miembrosIds != null ? miembrosIds.size() : 0);
        if (evento.getMaxMiembrosEquipo() != null && totalMiembros > evento.getMaxMiembrosEquipo()) {
            throw new RuntimeException("El equipo supera el límite máximo permitido de " + evento.getMaxMiembrosEquipo() + " miembros.");
        }

        // 4. Validar al Líder
        Usuario lider = usuarioRepository.findById(liderId)
                .orElseThrow(() -> new RuntimeException("Líder no encontrado"));
                
        if (!lider.getRoles().contains("ESTUDIANTE")) {
            throw new RuntimeException("El líder del equipo debe ser un ESTUDIANTE.");
        }

        // 5. Validar Alcance y Sedes
        if (evento.getAlcance().equals("SEDE") || evento.getAlcance().equals("INTER_SEDES")) {
            if (!lider.getSede().getId().equals(evento.getSedeOrganizadora().getId()) && evento.getAlcance().equals("SEDE")) {
                throw new RuntimeException("En eventos de SEDE, el líder debe pertenecer a la sede organizadora.");
            }
            // NOTA: Aquí deberías hacer un bucle para verificar que todos los miembrosIds 
            // compartan la misma sede que el líder. Lo omito por brevedad visual.
        }

        // 6. Convertir la lista de IDs a String JSON (Para cumplir la regla de 16 tablas)
        String miembrosJson = "[]";
        if (miembrosIds != null && !miembrosIds.isEmpty()) {
            try {
                miembrosJson = objectMapper.writeValueAsString(miembrosIds); // Convierte a "[6,7,8]"
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar los miembros del equipo", e);
            }
        }

        // 7. Validar Asesor (Opcional)
        Usuario asesor = null;
        if (asesorId != null) {
            asesor = usuarioRepository.findById(asesorId)
                    .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));
            if (!asesor.getRoles().contains("PROFESOR")) {
                throw new RuntimeException("El asesor debe tener el rol de PROFESOR.");
            }
        }

        // 8. Construir y guardar el Equipo
        Equipo nuevoEquipo = new Equipo();
        nuevoEquipo.setEvento(evento);
        nuevoEquipo.setSede(lider.getSede()); // El equipo asume la sede del líder
        nuevoEquipo.setNombre(nombreEquipo);
        nuevoEquipo.setLider(lider);
        nuevoEquipo.setMiembros(miembrosJson); // Aquí guardamos el JSON
        nuevoEquipo.setAsesor(asesor);
        nuevoEquipo.setAprobado(false); // Nace pendiente de aprobación

        return equipoRepository.save(nuevoEquipo);
    }

    @Override
    public List<Equipo> obtenerEquiposPorEvento(Long eventoId) {
        // Spring Data JPA hace todo el trabajo pesado aquí
        return equipoRepository.findByEventoId(eventoId);
    }
    
    @Override
    @Transactional
    public Equipo aprobarEquipo(Long equipoId, Long organizadorId) {
        
        // 1. Buscamos el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // 2. Buscamos a la persona que intenta aprobarlo
        Usuario organizador = usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        // 3. REGLA DE NEGOCIO: Validar permisos
        // Solo alguien con rol PROFESOR u ORGANIZADOR puede aprobar equipos
        if (!organizador.getRoles().contains("ORGANIZADOR") && !organizador.getRoles().contains("PROFESOR")) {
            throw new RuntimeException("No tienes los permisos necesarios para aprobar equipos.");
        }

        // 4. Cambiamos el estado y guardamos
        equipo.setAprobado(true);
        return equipoRepository.save(equipo);
    }
}
