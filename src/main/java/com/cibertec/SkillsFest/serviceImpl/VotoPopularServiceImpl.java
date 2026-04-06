package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.VotoPopular;
import com.cibertec.SkillsFest.repository.IVotoPopularRepository;
import com.cibertec.SkillsFest.service.VotoPopularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VotoPopularServiceImpl implements VotoPopularService {

    private final IVotoPopularRepository votoPopularRepository;

    @Override
    public VotoPopular votar(VotoPopular voto) {
        if (voto.getVotadoEn() == null) voto.setVotadoEn(new Date());
        return votoPopularRepository.save(voto);
    }

    @Override
    public Optional<VotoPopular> obtenerPorId(Long id) {
        return votoPopularRepository.findById(id);
    }

    @Override
    public List<VotoPopular> listarPorProyecto(Long proyectoId) {
        return votoPopularRepository.findByProyectoId(proyectoId);
    }

    @Override
    public long contarVotosPorProyecto(Long proyectoId) {
        return votoPopularRepository.countByProyectoId(proyectoId);
    }

    @Override
    public boolean yaVotoIp(Long proyectoId, String ipAddress) {
        return votoPopularRepository.existsByProyectoIdAndIpAddress(proyectoId, ipAddress);
    }

    @Override
    public boolean yaVotoUsuario(Long proyectoId, Long usuarioId) {
        return usuarioId != null && votoPopularRepository.existsByProyectoIdAndUsuarioId(proyectoId, usuarioId);
    }

    @Override
    public void eliminar(Long id) {
        votoPopularRepository.deleteById(id);
    }
}