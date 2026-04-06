package com.cibertec.SkillsFest.service;

import com.cibertec.SkillsFest.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<Usuario> obtenerTodos();
    Page<Usuario> obtenerTodosPaginado(Pageable pageable);
    Optional<Usuario> obtenerPorId(Long id);
    Optional<Usuario> obtenerPorEmail(String email);
    Usuario crear(Usuario usuario, Long sedeId);
    Usuario actualizar(Long id, Usuario usuario);
    void eliminar(Long id);
    List<Usuario> obtenerPorSede(Long sedeId);
}