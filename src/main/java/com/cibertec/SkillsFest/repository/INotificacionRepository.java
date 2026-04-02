package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface INotificacionRepository extends JpaRepository<Notificacion,Long> {
}
