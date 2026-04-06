package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.repository.ISedeRepository;
import com.cibertec.SkillsFest.service.ISedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements ISedeService {

    private final ISedeRepository sedeRepository;

    @Override
    public List<Sede> obtenerSedesActivas() {
        return sedeRepository.findByActivoTrue();
    }
}