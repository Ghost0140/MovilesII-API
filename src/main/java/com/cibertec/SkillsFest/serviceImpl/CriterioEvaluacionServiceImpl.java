package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import com.cibertec.SkillsFest.repository.ICriterioEvaluacionRepository;
import com.cibertec.SkillsFest.service.ICriterioEvaluacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CriterioEvaluacionServiceImpl implements ICriterioEvaluacionService {

    private final ICriterioEvaluacionRepository criterioRepository;

    @Override
    public List<CriterioEvaluacion> obtenerTodos() {
        return criterioRepository.findAll();
    }

    @Override
    public Page<CriterioEvaluacion> obtenerTodosPaginado(Pageable pageable) {
        return criterioRepository.findAll(pageable);
    }

    @Override
    public Optional<CriterioEvaluacion> obtenerPorId(Long id) {
        return criterioRepository.findById(id);
    }

    @Override
    public CriterioEvaluacion crear(CriterioEvaluacion criterio) {
        validarPesoEvento(criterio.getEvento().getId(), criterio.getPeso(), null);
        return criterioRepository.save(criterio);
    }

    @Override
    public CriterioEvaluacion actualizar(Long id, CriterioEvaluacion criterioActualizado) {
        CriterioEvaluacion criterio = criterioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        BigDecimal nuevoPeso = criterioActualizado.getPeso() != null ? criterioActualizado.getPeso() : criterio.getPeso();
        validarPesoEvento(criterio.getEvento().getId(), nuevoPeso, id);

        if (criterioActualizado.getNombre() != null) criterio.setNombre(criterioActualizado.getNombre());
        if (criterioActualizado.getPeso() != null) criterio.setPeso(criterioActualizado.getPeso());
        if (criterioActualizado.getPuntajeMaximo() != null) criterio.setPuntajeMaximo(criterioActualizado.getPuntajeMaximo());

        return criterioRepository.save(criterio);
    }

    @Override
    public void eliminar(Long id) {
        CriterioEvaluacion criterio = criterioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));
        criterioRepository.delete(criterio);
    }

    @Override
    public List<CriterioEvaluacion> obtenerPorEvento(Long eventoId) {
        return criterioRepository.findByEventoId(eventoId);
    }

    private void validarPesoEvento(Long eventoId, BigDecimal nuevoPeso, Long criterioIdActual) {
        List<CriterioEvaluacion> criterios = criterioRepository.findByEventoId(eventoId);

        BigDecimal suma = criterios.stream()
                .filter(c -> criterioIdActual == null || !c.getId().equals(criterioIdActual))
                .map(c -> c.getPeso() != null ? c.getPeso() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        suma = suma.add(nuevoPeso != null ? nuevoPeso : BigDecimal.ZERO);

        if (suma.compareTo(new BigDecimal("100.00")) > 0) {
            throw new RuntimeException("La suma de pesos de criterios no puede superar 100");
        }
    }
}