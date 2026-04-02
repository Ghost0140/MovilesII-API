package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.PortafolioPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IPortafolioPublicoRepository extends JpaRepository<PortafolioPublico,Long> {
}
