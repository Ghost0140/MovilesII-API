package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.RankingSede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IRankingSedeService {
    List<RankingSede> obtenerTodos();
    Page<RankingSede> obtenerTodosPaginado(Pageable pageable);
    Optional<RankingSede> obtenerPorId(Long id);
    RankingSede crear(RankingSede ranking);
    RankingSede actualizar(Long id, RankingSede ranking);
    void eliminar(Long id);
    List<RankingSede> obtenerPorEvento(Long eventoId);
}