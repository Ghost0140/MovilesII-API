package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.RankingSede;
import com.cibertec.SkillsFest.repository.IRankingSedeRepository;
import com.cibertec.SkillsFest.service.IRankingSedeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RankingSedeServiceImpl implements IRankingSedeService {

    private final IRankingSedeRepository rankingSedeRepository;

    @Override
    public List<RankingSede> obtenerTodos() {
        return rankingSedeRepository.findAll();
    }

    @Override
    public Page<RankingSede> obtenerTodosPaginado(Pageable pageable) {
        return rankingSedeRepository.findAll(pageable);
    }

    @Override
    public Optional<RankingSede> obtenerPorId(Long id) {
        return rankingSedeRepository.findById(id);
    }

    @Override
    public RankingSede crear(RankingSede ranking) {
        return rankingSedeRepository.save(ranking);
    }

    @Override
    public RankingSede actualizar(Long id, RankingSede rankingActualizado) {
        RankingSede ranking = rankingSedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ranking de sede no encontrado"));

        if (rankingActualizado.getPosicion() != null) ranking.setPosicion(rankingActualizado.getPosicion());
        if (rankingActualizado.getPuntosTotales() != null) ranking.setPuntosTotales(rankingActualizado.getPuntosTotales());
        if (rankingActualizado.getProyectosPresentados() != null) ranking.setProyectosPresentados(rankingActualizado.getProyectosPresentados());

        return rankingSedeRepository.save(ranking);
    }

    @Override
    public void eliminar(Long id) {
        RankingSede ranking = rankingSedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ranking de sede no encontrado"));
        rankingSedeRepository.delete(ranking);
    }

    @Override
    public List<RankingSede> obtenerPorEvento(Long eventoId) {
        return rankingSedeRepository.findByEventoId(eventoId);
    }
}