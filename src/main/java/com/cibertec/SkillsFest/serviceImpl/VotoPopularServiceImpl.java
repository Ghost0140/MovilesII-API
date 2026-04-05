package com.cibertec.SkillsFest.serviceImpl;


import com.cibertec.SkillsFest.entity.VotoPopular;
import com.cibertec.SkillsFest.repository.IVotoPopularRepository;
import com.cibertec.SkillsFest.service.VotoPopularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotoPopularServiceImpl implements VotoPopularService {

    private final IVotoPopularRepository votoPopularRepository;

    @Override
    public VotoPopular votar(VotoPopular voto) {
        if (voto.getVotadoEn() == null) {
            voto.setVotadoEn(new Date());
        }
        return votoPopularRepository.save(voto);
    }

    @Override
    public Optional<VotoPopular> obtenerPorId(Long id) {
        return votoPopularRepository.findById(id);
    }

    @Override
    public List<VotoPopular> listarPorProyecto(Long proyectoId) {
        return votoPopularRepository.findAll().stream()
                .filter(v -> v.getProyecto() != null && v.getProyecto().getId().equals(proyectoId))
                .collect(Collectors.toList());
    }

    @Override
    public long contarVotosPorProyecto(Long proyectoId) {
        return listarPorProyecto(proyectoId).size();
    }

    @Override
    public boolean yaVotoIp(Long proyectoId, String ipAddress) {
        return votoPopularRepository.findAll().stream()
                .anyMatch(v -> v.getProyecto() != null && v.getProyecto().getId().equals(proyectoId)
                        && v.getIpAddress() != null && v.getIpAddress().equals(ipAddress));
    }

    @Override
    public boolean yaVotoUsuario(Long proyectoId, Long usuarioId) {
        if (usuarioId == null) return false;
        return votoPopularRepository.findAll().stream()
                .anyMatch(v -> v.getProyecto() != null && v.getProyecto().getId().equals(proyectoId)
                        && v.getUsuario() != null && v.getUsuario().getId().equals(usuarioId));
    }

    @Override
    public void eliminar(Long id) {
        votoPopularRepository.deleteById(id);
    }
}