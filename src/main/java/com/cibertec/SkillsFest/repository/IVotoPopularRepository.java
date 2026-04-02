package com.cibertec.SkillsFest.repository;


import com.cibertec.SkillsFest.entity.VotoPopular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IVotoPopularRepository extends JpaRepository<VotoPopular,Long> {
}
