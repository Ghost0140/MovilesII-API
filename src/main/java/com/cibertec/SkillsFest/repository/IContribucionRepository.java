package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Contribucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IContribucionRepository extends JpaRepository<Contribucion, Long> {
    Optional<Contribucion> findByRepositorioIdAndUsuarioId(Long repositorioId, Long usuarioId);

    List<Contribucion> findByRepositorioId(Long repositorioId);

    List<Contribucion> findByUsuarioId(Long usuarioId);
}

