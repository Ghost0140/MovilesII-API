package com.cibertec.SkillsFest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SkillsFest.entity.Sede;
import com.cibertec.SkillsFest.service.ISedeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sedes")
@CrossOrigin(origins = "*") // Permiso para React
@RequiredArgsConstructor
public class SedeController {
	
	private final ISedeService sedeService;

    // GET: http://localhost:9090/api/sedes
    @GetMapping
    public ResponseEntity<?> listarSedes() {
        List<Sede> sedes = sedeService.obtenerSedesActivas();
        
        if (sedes.isEmpty()) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sedes activas en el sistema."), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }
}
