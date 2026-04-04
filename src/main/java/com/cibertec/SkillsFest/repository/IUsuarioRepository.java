package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IUsuarioRepository extends JpaRepository<Usuario,Long> {
	
	// Vital para el Login con Spring Security
    Optional<Usuario> findByEmail(String email);

    // Para validaciones al registrar un nuevo usuario
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByCodigoEstudiante(String codigoEstudiante);

    // Como los roles son un String "ESTUDIANTE,PROFESOR", usamos Containing
    // Ejemplo de uso: repository.findByRolesContaining("JURADO")
    List<Usuario> findByRolesContaining(String rol);
    
    // Listar usuarios activos de una sede específica
    List<Usuario> findBySedeIdAndActivoTrue(Long sedeId);
}
