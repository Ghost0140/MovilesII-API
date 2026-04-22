package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.DashboardResponse;
import com.cibertec.SkillsFest.repository.IEventoRepository;
import com.cibertec.SkillsFest.repository.IProyectoRepository;
import com.cibertec.SkillsFest.repository.IRankingAreaRepository;
import com.cibertec.SkillsFest.repository.IRankingSedeRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IUsuarioRepository usuarioRepository;
    private final IEventoRepository eventoRepository;
    private final IProyectoRepository proyectoRepository;
    private final IRankingAreaRepository rankingAreaRepository;
    private final IRankingSedeRepository rankingSedeRepository;

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        long totalUsuariosActivos = usuarioRepository.findByActivo(true).size();

        long totalEventos = eventoRepository.findAll().stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(String.valueOf(e.getEstado())))
                .count();

        long totalProyectos = proyectoRepository.findAll().stream()
                .filter(p -> !"ELIMINADO".equalsIgnoreCase(String.valueOf(p.getEstado())))
                .count();

        List<Map<String, Object>> topUsuariosRadar = new ArrayList<>();

        rankingAreaRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        r -> r.getScore() != null ? r.getScore() : BigDecimal.ZERO,
                        Comparator.reverseOrder()
                ))
                .limit(5)
                .forEach(r -> {
                    Map<String, Object> item = new LinkedHashMap<>();

                    item.put("usuarioId", r.getUsuario() != null ? r.getUsuario().getId() : null);
                    item.put(
                            "usuarioNombre",
                            r.getUsuario() != null
                                    ? (r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos())
                                    : "Sin usuario"
                    );
                    item.put("area", r.getArea() != null ? r.getArea() : "-");
                    item.put("score", r.getScore() != null ? r.getScore() : BigDecimal.ZERO);

                    topUsuariosRadar.add(item);
                });

        List<Map<String, Object>> topSedes = new ArrayList<>();

        rankingSedeRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        r -> r.getPuntosTotales() != null ? r.getPuntosTotales() : BigDecimal.ZERO,
                        Comparator.reverseOrder()
                ))
                .limit(5)
                .forEach(r -> {
                    Map<String, Object> item = new LinkedHashMap<>();

                    item.put("sedeId", r.getSede() != null ? r.getSede().getId() : null);
                    item.put("sedeNombre", r.getSede() != null ? r.getSede().getNombre() : "Sin sede");
                    item.put("puntosTotales", r.getPuntosTotales() != null ? r.getPuntosTotales() : BigDecimal.ZERO);
                    item.put("proyectosPresentados", r.getProyectosPresentados() != null ? r.getProyectosPresentados() : 0);

                    topSedes.add(item);
                });

        List<Map<String, Object>> ultimosEventos = new ArrayList<>();

        eventoRepository.findAll().stream()
                .filter(e -> !"ELIMINADO".equalsIgnoreCase(String.valueOf(e.getEstado())))
                .sorted(Comparator.comparing(
                        e -> e.getCreadoEn() != null ? e.getCreadoEn() : LocalDateTime.MIN,
                        Comparator.reverseOrder()
                ))
                .limit(5)
                .forEach(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();

                    item.put("eventoId", e.getId());
                    item.put("nombre", e.getNombre() != null ? e.getNombre() : "-");
                    item.put("tipo", e.getTipo() != null ? e.getTipo() : "-");
                    item.put("estado", e.getEstado() != null ? e.getEstado() : "-");
                    item.put("fechaEvento", e.getFechaEvento());

                    ultimosEventos.add(item);
                });

        DashboardResponse response = new DashboardResponse(
                totalUsuariosActivos,
                totalEventos,
                totalProyectos,
                topUsuariosRadar,
                topSedes,
                ultimosEventos
        );

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resumen del dashboard obtenido correctamente",
                "data", response
        ));
    }
}