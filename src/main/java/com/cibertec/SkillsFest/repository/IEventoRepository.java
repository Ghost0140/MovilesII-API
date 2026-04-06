package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByEstado(String estado);
    List<Evento> findBySedeOrganizadoraIdAndEstadoNot(Long sedeId, String estado);
    List<Evento> findByAlcanceAndEstado(String alcance, String estado);
}