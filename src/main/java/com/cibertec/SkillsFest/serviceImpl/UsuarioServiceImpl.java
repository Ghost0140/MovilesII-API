package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import com.cibertec.SkillsFest.service.IUsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final ISedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Page<Usuario> obtenerTodosPaginado(Boolean activo, Pageable pageable) {
        if (activo == null) {
            return usuarioRepository.findAll(pageable);
        }
        return usuarioRepository.findByActivo(activo, pageable);
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario crear(Usuario usuario, Long sedeId) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        if (usuario.getNumeroDocumento() != null && usuarioRepository.existsByNumeroDocumento(usuario.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un usuario con ese documento");
        }

        if (usuario.getGithubUsername() != null && !usuario.getGithubUsername().isBlank()
                && usuarioRepository.existsByGithubUsername(usuario.getGithubUsername())) {
            throw new RuntimeException("Ya existe un usuario con ese githubUsername");
        }
        
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        usuario.setSede(sede);
        usuario.setActivo(true);

        if (usuario.getRoles() == null || usuario.getRoles().isBlank()) {
            usuario.setRoles("ESTUDIANTE");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioActualizado.getNombres() != null) usuario.setNombres(usuarioActualizado.getNombres());
        if (usuarioActualizado.getApellidos() != null) usuario.setApellidos(usuarioActualizado.getApellidos());
        if (usuarioActualizado.getSede() != null) usuario.setSede(usuarioActualizado.getSede());
        if (usuarioActualizado.getCarrera() != null) usuario.setCarrera(usuarioActualizado.getCarrera());
        if (usuarioActualizado.getCiclo() != null) usuario.setCiclo(usuarioActualizado.getCiclo());

        if (usuarioActualizado.getGithubUsername() != null
                && !usuarioActualizado.getGithubUsername().equals(usuario.getGithubUsername())) {
            if (usuarioRepository.existsByGithubUsername(usuarioActualizado.getGithubUsername())) {
                throw new RuntimeException("Ya existe un usuario con ese githubUsername");
            }
            usuario.setGithubUsername(usuarioActualizado.getGithubUsername());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario cambiarActivo(Long id, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> obtenerPorSede(Long sedeId) {
        return usuarioRepository.findBySedeIdAndActivoTrue(sedeId);
    }
}