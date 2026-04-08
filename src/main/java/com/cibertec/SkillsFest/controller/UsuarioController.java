package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.UsuarioCreateRequest;
import com.cibertec.SkillsFest.dto.UsuarioEstadoRequest;
import com.cibertec.SkillsFest.dto.UsuarioResponse;
import com.cibertec.SkillsFest.dto.UsuarioUpdateRequest;
import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.exception.ResourceNotFoundException;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final ISedeRepository sedeRepository;

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuarios = usuarioService.obtenerTodosPaginado(activo, pageable);

        List<UsuarioResponse> data = usuarios.getContent()
                .stream()
                .map(ApiMapper::toUsuarioResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuarios obtenidos exitosamente",
                "data", data,
                "paginaActual", usuarios.getNumber(),
                "totalPaginas", usuarios.getTotalPages(),
                "totalElementos", usuarios.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario obtenido",
                "data", ApiMapper.toUsuarioResponse(usuario)
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioCreateRequest request) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombres(request.nombres());
        nuevoUsuario.setApellidos(request.apellidos());
        nuevoUsuario.setEmail(request.email());
        nuevoUsuario.setPassword(request.password());
        nuevoUsuario.setNumeroDocumento(request.numeroDocumento());
        nuevoUsuario.setCarrera(request.carrera());
        nuevoUsuario.setCiclo(request.ciclo());
        nuevoUsuario.setCodigoEstudiante(request.codigoEstudiante());
        nuevoUsuario.setGithubUsername(request.githubUsername());

        Usuario guardado = usuarioService.crear(nuevoUsuario, request.sedeId());

        return new ResponseEntity<>(Map.of(
                "mensaje", "Usuario creado exitosamente",
                "data", ApiMapper.toUsuarioResponse(guardado)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request) {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombres(request.nombres());
        usuarioActualizado.setApellidos(request.apellidos());
        usuarioActualizado.setCarrera(request.carrera());
        usuarioActualizado.setCiclo(request.ciclo());
        usuarioActualizado.setGithubUsername(request.githubUsername());

        Sede sede = sedeRepository.findById(request.sedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        usuarioActualizado.setSede(sede);

        Usuario usuario = usuarioService.actualizar(id, usuarioActualizado);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario actualizado exitosamente",
                "data", ApiMapper.toUsuarioResponse(usuario)
        ));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> cambiarActivo(@PathVariable Long id, @Valid @RequestBody UsuarioEstadoRequest request) {
        Usuario usuario = usuarioService.cambiarActivo(id, request.activo());

        return ResponseEntity.ok(Map.of(
                "mensaje", request.activo() ? "Usuario activado" : "Usuario desactivado",
                "data", ApiMapper.toUsuarioResponse(usuario)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado exitosamente"));
    }
}