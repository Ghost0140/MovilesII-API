package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.service.IPortafolioPublicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/portafolios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortafolioPublicoController {

    private final IPortafolioPublicoService portafolioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<PortafolioPublico> portfolio = portafolioService.obtenerPorId(id);

        if (portfolio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Portafolio no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", portfolio.get()
        ));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long usuarioId) {
        Optional<PortafolioPublico> portfolio = portafolioService.obtenerPorUsuario(usuarioId);

        if (portfolio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Portafolio no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", portfolio.get()
        ));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> obtenerPorSlug(@PathVariable String slug) {
        Optional<PortafolioPublico> portfolio = portafolioService.obtenerPorSlug(slug);

        if (portfolio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Portafolio no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", portfolio.get()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PortafolioPublico request) {
        PortafolioPublico actualizado = portafolioService.actualizar(id, request);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio actualizado correctamente",
                "data", actualizado
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        portafolioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Portafolio eliminado correctamente"));
    }
}