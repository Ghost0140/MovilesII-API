package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.VotoPopular;
import java.util.List;
import java.util.Optional;

public interface VotoPopularService {
    VotoPopular votar(VotoPopular voto);
    Optional<VotoPopular> obtenerPorId(Long id);
    List<VotoPopular> listarPorProyecto(Long proyectoId);
    long contarVotosPorProyecto(Long proyectoId);
    boolean yaVotoIp(Long proyectoId, String ipAddress);
    boolean yaVotoUsuario(Long proyectoId, Long usuarioId);
    void eliminar(Long id);
}