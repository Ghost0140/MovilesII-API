package com.cibertec.SkillsFest.controller;

import com.cibertec.SkillsFest.entity.Notificacion;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.INotificacionRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.InvitacionEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppNotificacionController {

    private final IUsuarioRepository usuarioRepository;
    private final INotificacionRepository notificacionRepository;
    private final InvitacionEquipoService invitacionEquipoService;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        invitacionEquipoService.rechazarInvitacionesVencidas();
        return ResponseEntity.ok(notificacionRepository.findByUsuarioIdAndActivoTrueOrderByCreadoEnDesc(usuario.getId()));
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (notificacion.getUsuario() == null || !notificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Notificación no disponible");
        }

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
        return ResponseEntity.ok().build();
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }
}
