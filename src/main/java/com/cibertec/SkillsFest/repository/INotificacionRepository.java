package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface INotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdAndActivoTrueOrderByCreadoEnDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeidaFalseAndActivoTrueOrderByCreadoEnDesc(Long usuarioId);
}