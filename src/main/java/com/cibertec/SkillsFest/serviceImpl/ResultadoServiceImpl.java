package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Evaluacion;
import com.cibertec.SkillsFest.entity.Proyecto;
import com.cibertec.SkillsFest.entity.Resultado;
import com.cibertec.SkillsFest.entity.VotoPopular;
import com.cibertec.SkillsFest.repository.IEvaluacionRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IResultadoRepository;
import com.cibertec.SkillsFest.repository.IVotoPopularRepository;
import com.cibertec.SkillsFest.service.IResultadoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ResultadoServiceImpl implements IResultadoService {

    private final IResultadoRepository resultadoRepository;
    private final IProyectoRepository proyectoRepository;
    private final IEvaluacionRepository evaluacionRepository;
    private final IVotoPopularRepository votoPopularRepository;

    @Override
    public List<Resultado> obtenerTodos() {
        return resultadoRepository.findAll();
    }

    @Override
    public Page<Resultado> obtenerTodosPaginado(Pageable pageable) {
        return resultadoRepository.findAll(pageable);
    }

    @Override
    public Optional<Resultado> obtenerPorId(Long id) {
        return resultadoRepository.findById(id);
    }

    @Override
    public Resultado crear(Resultado resultado) {
        return resultadoRepository.save(resultado);
    }

    @Override
    public Resultado actualizar(Long id, Resultado resultadoActualizado) {
        Resultado resultado = resultadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));

        if (resultadoActualizado.getPuntajeJurados() != null) resultado.setPuntajeJurados(resultadoActualizado.getPuntajeJurados());
        if (resultadoActualizado.getPuntajePopular() != null) resultado.setPuntajePopular(resultadoActualizado.getPuntajePopular());
        if (resultadoActualizado.getPuntajeTotal() != null) resultado.setPuntajeTotal(resultadoActualizado.getPuntajeTotal());
        if (resultadoActualizado.getPosicion() != null) resultado.setPosicion(resultadoActualizado.getPosicion());
        if (resultadoActualizado.getCategoriaPremio() != null) resultado.setCategoriaPremio(resultadoActualizado.getCategoriaPremio());
        if (resultadoActualizado.getPublicado() != null) resultado.setPublicado(resultadoActualizado.getPublicado());

        return resultadoRepository.save(resultado);
    }

    @Override
    public void eliminar(Long id) {
        Resultado resultado = resultadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
        resultadoRepository.delete(resultado);
    }

    @Override
    public List<Resultado> obtenerPorEvento(Long eventoId) {
        return resultadoRepository.findByEventoId(eventoId);
    }

    @Override
    public Resultado calcularResultados(Long eventoId, Long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!proyecto.getEvento().getId().equals(eventoId)) {
            throw new RuntimeException("El proyecto no pertenece al evento indicado");
        }

        List<Evaluacion> evaluaciones = evaluacionRepository.findByProyectoId(proyectoId);
        List<VotoPopular> votos = votoPopularRepository.findByProyectoId(proyectoId);

        double promedioJurados = evaluaciones.stream()
                .mapToDouble(e -> e.getPuntaje() != null ? e.getPuntaje().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        double puntajePopular = votos.size();
        double puntajeTotal = promedioJurados + puntajePopular;

        Resultado resultado = resultadoRepository.findByProyectoId(proyectoId)
                .orElse(new Resultado());

        resultado.setEvento(proyecto.getEvento());
        resultado.setProyecto(proyecto);
        resultado.setPuntajeJurados(bd(promedioJurados));
        resultado.setPuntajePopular(bd(puntajePopular));
        resultado.setPuntajeTotal(bd(puntajeTotal));
        resultado.setPublicado(false);

        Resultado guardado = resultadoRepository.save(resultado);
        recalcularPosiciones(eventoId);

        return guardado;
    }

    @Override
    public void publicarResultados(Long eventoId) {
        List<Resultado> resultados = resultadoRepository.findByEventoId(eventoId);

        if (resultados.isEmpty()) {
            throw new RuntimeException("No hay resultados para publicar en este evento");
        }

        recalcularPosiciones(eventoId);
        resultados = resultadoRepository.findByEventoId(eventoId);

        for (Resultado r : resultados) {
            r.setPublicado(true);
            r.setFechaPublicacion(new Date());

            if (r.getPosicion() != null) {
                if (r.getPosicion() == 1) r.setCategoriaPremio("ORO");
                else if (r.getPosicion() == 2) r.setCategoriaPremio("PLATA");
                else if (r.getPosicion() == 3) r.setCategoriaPremio("BRONCE");
            }

            resultadoRepository.save(r);
        }
    }

    private void recalcularPosiciones(Long eventoId) {
        List<Resultado> resultados = resultadoRepository.findByEventoId(eventoId)
                .stream()
                .sorted(Comparator.comparing(Resultado::getPuntajeTotal).reversed())
                .toList();

        int posicion = 1;
        for (Resultado r : resultados) {
            r.setPosicion(posicion++);
            resultadoRepository.save(r);
        }
    }

    private BigDecimal bd(double valor) {
        return BigDecimal.valueOf(Math.round(valor * 100.0) / 100.0);
    }
}