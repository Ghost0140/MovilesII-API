package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findByEventoIdAndEstadoNot(Long eventoId, String estado);

    List<Proyecto> findByEstado(String estado);

    boolean existsByRepositorioUrlAndEstadoNot(String repositorioUrl, String estado);

    boolean existsByRepositorioUrlAndIdNotAndEstadoNot(String repositorioUrl, Long id, String estado);
}