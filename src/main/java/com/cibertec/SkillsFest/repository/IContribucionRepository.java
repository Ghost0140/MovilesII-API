package com.cibertec.SkillsFest.repository;

import com.cibertec.SkillsFest.entity.Contribucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IContribucionRepository extends JpaRepository<Contribucion,Long> {
}
