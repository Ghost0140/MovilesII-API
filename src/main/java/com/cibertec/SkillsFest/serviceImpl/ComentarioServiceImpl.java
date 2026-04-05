package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Comentario;
import com.cibertec.SkillsFest.repository.IComentarioRepository;
import com.cibertec.SkillsFest.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {

    private final IComentarioRepository comentarioRepository;

    @Override
    public Comentario guardar(Comentario comentario) {
        if (comentario.getCreadoEn() == null) {
            comentario.setCreadoEn(new Date());
        }
        if (comentario.getVisible() == null) {
            comentario.setVisible(true);
        }
        return comentarioRepository.save(comentario);
    }

    @Override
    public Optional<Comentario> obtenerPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    @Override
    public List<Comentario> listarTodos() {
        return comentarioRepository.findAll();
    }

    @Override
    public List<Comentario> listarPorProyecto(Long proyectoId) {
        return comentarioRepository.findAll().stream()
                .filter(c -> c.getProyecto() != null && c.getProyecto().getId().equals(proyectoId))
                .filter(Comentario::getVisible)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        comentarioRepository.findById(id).ifPresent(comentario -> {
            comentario.setVisible(false);
            comentarioRepository.save(comentario);
        });
    }
}