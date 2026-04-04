package com.cibertec.SkillsFest.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.service.ISedeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements ISedeService{

	private final ISedeRepository sedeRepository;

    @Override
    public List<Sede> obtenerSedesActivas() {
        // Usamos el método que creamos hace un rato en el SedeRepository
        return sedeRepository.findByActivoTrue();
    }
}
