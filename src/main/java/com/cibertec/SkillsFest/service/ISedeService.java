package com.cibertec.SkillsFest.service;

import java.util.List;

import com.cibertec.SkillsFest.entity.Sede;

public interface ISedeService {
	// Solo necesitamos listar las sedes que estén activas
    List<Sede> obtenerSedesActivas();
}
