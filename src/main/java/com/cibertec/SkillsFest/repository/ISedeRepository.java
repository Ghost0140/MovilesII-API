package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ISedeRepository extends JpaRepository<Sede,Long> {
}
