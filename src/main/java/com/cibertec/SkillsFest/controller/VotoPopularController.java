package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.VotoPopular;
import com.cibertec.SkillsFest.service.VotoPopularService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votos")
@RequiredArgsConstructor
public class VotoPopularController {

    private final VotoPopularService votoPopularService;

    @PostMapping
    public ResponseEntity<?> votar(@RequestBody VotoPopular voto) {
        Long proyectoId = voto.getProyecto().getId();
        String ip = voto.getIpAddress();
        Long usuarioId = voto.getUsuario() != null ? voto.getUsuario().getId() : null;

        if (votoPopularService.yaVotoIp(proyectoId, ip)) {
            return ResponseEntity.badRequest().body("Ya has votado desde esta IP");
        }
        if (usuarioId != null && votoPopularService.yaVotoUsuario(proyectoId, usuarioId)) {
            return ResponseEntity.badRequest().body("Ya has votado como usuario registrado");
        }

        VotoPopular nuevo = votoPopularService.votar(voto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<VotoPopular>> listarPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(votoPopularService.listarPorProyecto(proyectoId));
    }

    @GetMapping("/proyecto/{proyectoId}/contar")
    public ResponseEntity<Long> contarVotos(@PathVariable Long proyectoId) {
        long total = votoPopularService.contarVotosPorProyecto(proyectoId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotoPopular> obtenerPorId(@PathVariable Long id) {
        return votoPopularService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        votoPopularService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}