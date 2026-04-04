package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Sede;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ISedeRepository extends JpaRepository<Sede,Long> {
	// Buscar una sede por su código (Ej: "CIB-LN")
    Optional<Sede> findByCodigo(String codigo);

    // Para el combo box en React: solo mostrar las sedes que están activas
    List<Sede> findByActivoTrue();
}
