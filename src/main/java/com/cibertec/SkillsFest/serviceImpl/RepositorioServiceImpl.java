package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Repositorio;
import com.cibertec.SkillsFest.repository.IRepositorioRepository;
import com.cibertec.SkillsFest.service.IRepositorioService;
import com.cibertec.SkillsFest.service.ITalentRadarService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepositorioServiceImpl implements IRepositorioService {

    private final IRepositorioRepository repositorioRepository;
    private final ITalentRadarService talentRadarService;

    @Override
    public List<Repositorio> obtenerTodos() {
        return repositorioRepository.findAll();
    }

    @Override
    public Optional<Repositorio> obtenerPorId(Long id) {
        return repositorioRepository.findById(id);
    }

    @Override
    public Optional<Repositorio> obtenerPorProyecto(Long proyectoId) {
        return repositorioRepository.findByProyectoId(proyectoId);
    }

    @Override
    public void reanalizarPorProyecto(Long proyectoId) {
        talentRadarService.analizarProyecto(proyectoId);
    }
}