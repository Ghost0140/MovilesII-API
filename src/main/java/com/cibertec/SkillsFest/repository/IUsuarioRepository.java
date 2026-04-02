package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IUsuarioRepository extends JpaRepository<Usuario,Long> {
}
