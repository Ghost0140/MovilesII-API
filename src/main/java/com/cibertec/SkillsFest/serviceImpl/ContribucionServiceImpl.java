package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.service.ContribucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContribucionServiceImpl implements ContribucionService {

    private final IContribucionRepository contribucionRepository;

    @Override
    public Contribucion guardar(Contribucion contribucion) {
        Optional<Contribucion> existente = contribucionRepository
                .findByRepositorioIdAndUsuarioId(
                        contribucion.getRepositorio().getId(),
                        contribucion.getUsuario().getId()
                );

        if (existente.isPresent() && (contribucion.getId() == null || !existente.get().getId().equals(contribucion.getId()))) {
            throw new RuntimeException("Ya existe una contribución para este repositorio y usuario");
        }

        if (contribucion.getAnalizadoEn() == null) {
            contribucion.setAnalizadoEn(LocalDateTime.now());
        }

        return contribucionRepository.save(contribucion);
    }

    @Override
    public Optional<Contribucion> obtenerPorId(Long id) {
        return contribucionRepository.findById(id);
    }

    @Override
    public List<Contribucion> listarTodos() {
        return contribucionRepository.findAll();
    }

    @Override
    public List<Contribucion> listarPorRepositorio(Long repositorioId) {
        return contribucionRepository.findByRepositorioId(repositorioId);
    }

    @Override
    public List<Contribucion> listarPorUsuario(Long usuarioId) {
        return contribucionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Contribucion> obtenerPorRepositorioYUsuario(Long repositorioId, Long usuarioId) {
        return contribucionRepository.findByRepositorioIdAndUsuarioId(repositorioId, usuarioId);
    }

    @Override
    public void eliminar(Long id) {
        contribucionRepository.deleteById(id);
    }
}