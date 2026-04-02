package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IComentarioRepository extends JpaRepository<Comentario,Long> {
}
