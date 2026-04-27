package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.dto.ApiMapper;
import com.cibertec.SkillsFest.dto.UsuarioResponse;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/app/perfil")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppPerfilController {

    private final IUsuarioRepository usuarioRepository;

    @PatchMapping("/github")
    public ResponseEntity<Map<String, Object>> actualizarGithub(
            @RequestBody ActualizarGithubRequest request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        String githubUsername = normalizarGithub(request.githubUsername());

        if (githubUsername == null || githubUsername.isBlank()) {
            throw new RuntimeException("Ingresa tu usuario de GitHub");
        }

        usuarioRepository.findByGithubUsername(githubUsername)
                .filter(u -> !Objects.equals(u.getId(), usuario.getId()))
                .ifPresent(u -> {
                    throw new RuntimeException("Ese usuario de GitHub ya está vinculado a otra cuenta");
                });

        usuario.setGithubUsername(githubUsername);
        Usuario actualizado = usuarioRepository.save(usuario);
        UsuarioResponse response = ApiMapper.toUsuarioResponse(actualizado);

        return ResponseEntity.ok(Map.of(
                "mensaje", "GitHub actualizado correctamente",
                "data", response
        ));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private String normalizarGithub(String raw) {
        if (raw == null) {
            return null;
        }

        String limpio = raw.trim();
        if (limpio.startsWith("@")) {
            limpio = limpio.substring(1);
        }

        return limpio;
    }

    public record ActualizarGithubRequest(String githubUsername) {
    }
}
