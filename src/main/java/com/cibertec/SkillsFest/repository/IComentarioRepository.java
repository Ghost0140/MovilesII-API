package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByProyectoIdAndVisibleTrueOrderByCreadoEnDesc(Long proyectoId);
}