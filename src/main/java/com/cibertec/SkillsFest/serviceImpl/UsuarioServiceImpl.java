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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final ISedeRepository sedeRepository;

    @Override
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findByActivo(true);
    }

    @Override
    public Page<Usuario> obtenerTodosPaginado(Pageable pageable) {
        return usuarioRepository.findByActivo(true, pageable);
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(Usuario::getActivo);
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .filter(Usuario::getActivo);
    }

    @Override
    public Usuario crear(Usuario usuario, Long sedeId) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
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
        if (usuarioActualizado.getCarrera() != null) usuario.setCarrera(usuarioActualizado.getCarrera());
        if (usuarioActualizado.getCiclo() != null) usuario.setCiclo(usuarioActualizado.getCiclo());
        if (usuarioActualizado.getGithubUsername() != null) usuario.setGithubUsername(usuarioActualizado.getGithubUsername());

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