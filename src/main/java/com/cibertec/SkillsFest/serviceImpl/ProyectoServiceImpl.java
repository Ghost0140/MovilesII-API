package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.entity.Evento;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IProyectoService;
import com.cibertec.SkillsFest.service.ITalentRadarService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProyectoServiceImpl implements IProyectoService {

    private final IProyectoRepository proyectoRepository;
    private final IEventoRepository eventoRepository;
    private final IEquipoRepository equipoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ITalentRadarService talentRadarService;

    @Override
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll()
                .stream()
                .filter(p -> !"ELIMINADO".equals(p.getEstado()))
                .toList();
    }

    @Override
    public Page<Proyecto> obtenerTodosPaginado(Pageable pageable) {
        return proyectoRepository.findAll(pageable);
    }

    @Override
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id)
                .filter(p -> !"ELIMINADO".equals(p.getEstado()));
    }

    @Override
    public Proyecto crear(Proyecto proyecto, Long eventoId, Long equipoId, Long usuarioId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        proyecto.setEvento(evento);

        if (equipoId != null && usuarioId != null) {
            throw new RuntimeException("El proyecto no puede ser individual y grupal a la vez");
        }

        if (equipoId == null && usuarioId == null) {
            throw new RuntimeException("Debe indicar equipoId o usuarioId");
        }

        if (equipoId != null) {
            Equipo equipo = equipoRepository.findById(equipoId)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            proyecto.setEquipo(equipo);
            proyecto.setUsuario(null);
        }

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            proyecto.setUsuario(usuario);
            proyecto.setEquipo(null);
        }

        proyecto.setEstado("BORRADOR");
        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto actualizar(Long id, Proyecto proyectoActualizado) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (proyectoActualizado.getTitulo() != null) proyecto.setTitulo(proyectoActualizado.getTitulo());
        if (proyectoActualizado.getResumen() != null) proyecto.setResumen(proyectoActualizado.getResumen());
        if (proyectoActualizado.getDescripcion() != null) proyecto.setDescripcion(proyectoActualizado.getDescripcion());

        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto cambiarEstado(Long id, String estado) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado(estado);
        return proyectoRepository.save(proyecto);
    }

    @Override
    public void eliminar(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado("ELIMINADO");
        proyectoRepository.save(proyecto);
    }

    @Override
    public List<Proyecto> obtenerPorEvento(Long eventoId) {
        return proyectoRepository.findByEventoIdAndEstadoNot(eventoId, "ELIMINADO");
    }

    @Override
    public List<Proyecto> obtenerPorEstado(String estado) {
        return proyectoRepository.findByEstado(estado);
    }

    @Override
    public Proyecto enviarProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado("ENVIADO");
        proyecto.setFechaEnvio(LocalDateTime.now());

        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto aprobarProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado("APROBADO");
        Proyecto aprobado = proyectoRepository.save(proyecto);

        try {
            talentRadarService.analizarProyecto(proyectoId);
        } catch (Exception e) {
            log.warn("Falló Talent Radar: {}", e.getMessage());
        }

        return aprobado;
    }

    @Override
    public Proyecto rechazarProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado("RECHAZADO");
        return proyectoRepository.save(proyecto);
    }
}