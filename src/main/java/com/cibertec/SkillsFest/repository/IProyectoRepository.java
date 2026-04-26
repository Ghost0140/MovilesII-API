package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Proyecto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Override
    @EntityGraph(attributePaths = {"evento", "equipo", "equipo.lider", "usuario"})
    Optional<Proyecto> findById(Long id);

    List<Proyecto> findByEventoIdAndEstadoNot(Long eventoId, String estado);

    List<Proyecto> findByEstado(String estado);

    boolean existsByEquipoIdAndEstadoNot(Long equipoId, String estado);

    boolean existsByRepositorioUrlAndEstadoNot(String repositorioUrl, String estado);

    boolean existsByRepositorioUrlAndIdNotAndEstadoNot(String repositorioUrl, Long id, String estado);
}
