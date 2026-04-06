package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public record CrearUsuarioRequest(
            String nombres,
            String apellidos,
            String email,
            String password,
            String numeroDocumento,
            Long sedeId,
            String carrera,
            Integer ciclo,
            String codigoEstudiante,
            String githubUsername,
            String roles
    ) {}

    public record ActualizarUsuarioRequest(
            String nombres,
            String apellidos,
            String carrera,
            Integer ciclo,
            String roles,
            String githubUsername
    ) {}

    @GetMapping
    public ResponseEntity<?> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuarios = usuarioService.obtenerTodosPaginado(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Usuarios obtenidos exitosamente");
        response.put("data", usuarios.getContent());
        response.put("paginaActual", usuarios.getNumber());
        response.put("totalPaginas", usuarios.getTotalPages());
        response.put("totalElementos", usuarios.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerPorId(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario obtenido",
                "data", usuario.get()
        ));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearUsuarioRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
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
            nuevoUsuario.setRoles(request.roles() != null ? request.roles() : "ESTUDIANTE");

            Usuario usuarioGuardado = usuarioService.crear(nuevoUsuario, request.sedeId());

            response.put("mensaje", "Usuario creado exitosamente");
            response.put("data", usuarioGuardado);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("error", "Error al crear usuario");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ActualizarUsuarioRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            Usuario usuarioActualizado = new Usuario();
            usuarioActualizado.setNombres(request.nombres());
            usuarioActualizado.setApellidos(request.apellidos());
            usuarioActualizado.setCarrera(request.carrera());
            usuarioActualizado.setCiclo(request.ciclo());
            usuarioActualizado.setRoles(request.roles());
            usuarioActualizado.setGithubUsername(request.githubUsername());

            Usuario usuario = usuarioService.actualizar(id, usuarioActualizado);

            response.put("mensaje", "Usuario actualizado exitosamente");
            response.put("data", usuario);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", "Error al actualizar usuario");
            response.put("detalle", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado exitosamente"));
    }
}