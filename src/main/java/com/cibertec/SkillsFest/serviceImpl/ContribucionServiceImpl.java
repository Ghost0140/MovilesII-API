package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Contribucion;
import com.cibertec.SkillsFest.repository.IContribucionRepository;
import com.cibertec.SkillsFest.service.ContribucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContribucionServiceImpl implements ContribucionService {

    private final IContribucionRepository contribucionRepository;

    @Override
    public Contribucion guardar(Contribucion contribucion) {
        // Validar unicidad (repositorio + usuario)
        Optional<Contribucion> existente = obtenerPorRepositorioYUsuario(
                contribucion.getRepositorio().getId(),
                contribucion.getUsuario().getId()
        );
        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe una contribución para este repositorio y usuario");
        }
        if (contribucion.getAnalizadoEn() == null) {
            contribucion.setAnalizadoEn(new Date());
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
        return contribucionRepository.findAll().stream()
                .filter(c -> c.getRepositorio() != null && c.getRepositorio().getId().equals(repositorioId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Contribucion> listarPorUsuario(Long usuarioId) {
        return contribucionRepository.findAll().stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Contribucion> obtenerPorRepositorioYUsuario(Long repositorioId, Long usuarioId) {
        return contribucionRepository.findAll().stream()
                .filter(c -> c.getRepositorio() != null && c.getRepositorio().getId().equals(repositorioId)
                        && c.getUsuario() != null && c.getUsuario().getId().equals(usuarioId))
                .findFirst();
    }

    @Override
    public void eliminar(Long id) {
        contribucionRepository.deleteById(id);
    }
}