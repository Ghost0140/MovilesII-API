package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IEventoRepository extends JpaRepository<Evento,Long> {
}
