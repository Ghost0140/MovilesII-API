package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.PortafolioEstadoRequest;
import com.cibertec.SkillsFest.dto.PortafolioResponse;
import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.service.IPortafolioPublicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portafolios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortafolioPublicoController {

    private final IPortafolioPublicoService portafolioService;

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<PortafolioResponse> data = portafolioService.obtenerTodos()
                .stream()
                .map(ApiMapper::toPortafolioResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolios obtenidos correctamente",
                "data", data
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        PortafolioPublico portfolio = portafolioService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", ApiMapper.toPortafolioResponse(portfolio)
        ));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long usuarioId) {
        PortafolioPublico portfolio = portafolioService.obtenerPorUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", ApiMapper.toPortafolioResponse(portfolio)
        ));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> obtenerPorSlug(@PathVariable String slug) {
        PortafolioPublico portfolio = portafolioService.obtenerPorSlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Portafolio obtenido",
                "data", ApiMapper.toPortafolioResponse(portfolio)
        ));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @Valid @RequestBody PortafolioEstadoRequest request) {
        PortafolioPublico actualizado = portafolioService.cambiarEstado(id, request.activo(), request.visible());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado del portafolio actualizado",
                "data", ApiMapper.toPortafolioResponse(actualizado)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        portafolioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Portafolio eliminado lógicamente"));
    }
}