package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.CriterioEvaluacion;
import com.cibertec.SkillsFest.entity.Evaluacion;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.ICriterioEvaluacionRepository;
import com.cibertec.SkillsFest.repository.IEvaluacionRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IEvaluacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements IEvaluacionService {

    private final IEvaluacionRepository evaluacionRepository;
    private final IProyectoRepository proyectoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ICriterioEvaluacionRepository criterioRepository;

    @Override
    public List<Evaluacion> obtenerTodos() {
        return evaluacionRepository.findAll();
    }

    @Override
    public Page<Evaluacion> obtenerTodosPaginado(Pageable pageable) {
        return evaluacionRepository.findAll(pageable);
    }

    @Override
    public Optional<Evaluacion> obtenerPorId(Long id) {
        return evaluacionRepository.findById(id);
    }

    @Override
    public Evaluacion crear(Long proyectoId, Long juradoId, Long criterioId, BigDecimal puntaje, String comentario) {
        if (evaluacionRepository.existsByProyectoIdAndJuradoIdAndCriterioId(proyectoId, juradoId, criterioId)) {
            throw new RuntimeException("Ya existe una evaluación para ese proyecto, jurado y criterio");
        }

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Usuario jurado = usuarioRepository.findById(juradoId)
                .orElseThrow(() -> new RuntimeException("Jurado no encontrado"));

        CriterioEvaluacion criterio = criterioRepository.findById(criterioId)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setProyecto(proyecto);
        evaluacion.setJurado(jurado);
        evaluacion.setCriterio(criterio);
        evaluacion.setPuntaje(puntaje);
        evaluacion.setComentario(comentario);
        evaluacion.setEvaluadoEn(new Date());

        return evaluacionRepository.save(evaluacion);
    }

    @Override
    public void eliminar(Long id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        evaluacionRepository.delete(evaluacion);
    }

    @Override
    public List<Evaluacion> obtenerPorProyecto(Long proyectoId) {
        return evaluacionRepository.findByProyectoId(proyectoId);
    }

    @Override
    public List<Evaluacion> obtenerPorJurado(Long juradoId) {
        return evaluacionRepository.findByJuradoId(juradoId);
    }
}