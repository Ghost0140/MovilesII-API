package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.VotoPopular;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVotoPopularRepository extends JpaRepository<VotoPopular, Long> {
    List<VotoPopular> findByProyectoId(Long proyectoId);
    long countByProyectoId(Long proyectoId);
    boolean existsByProyectoIdAndIpAddress(Long proyectoId, String ipAddress);
    boolean existsByProyectoIdAndUsuarioId(Long proyectoId, Long usuarioId);
}