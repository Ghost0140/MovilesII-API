package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    Optional<Usuario> findByGithubUsername(String githubUsername);

    List<Usuario> findBySedeIdAndActivoTrue(Long sedeId);
    Page<Usuario> findBySedeIdAndActivoTrue(Long sedeId, Pageable pageable);

    List<Usuario> findByActivo(Boolean activo);
    Page<Usuario> findByActivo(Boolean activo, Pageable pageable);

    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByGithubUsername(String githubUsername);
}