package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Evento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IEventoRepository extends JpaRepository<Evento,Long> {
	// Para la vista principal del alumno: Ver todos los eventos públicos
    List<Evento> findByEstado(String estado);

    // Para listar eventos de una sede específica (Ej: Lima Norte)
    List<Evento> findBySedeOrganizadoraId(Long sedeId);

    // Para ver eventos según su alcance (Ej: "INTER_SEDES")
    List<Evento> findByAlcanceAndEstado(String alcance, String estado);
}
