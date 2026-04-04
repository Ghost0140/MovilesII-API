package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
//Lo que hace: Cuando conviertas esto a JSON, escóndeme esos objetos fantasma
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mejora: Usar LAZY evita que Spring cargue los datos de la Sede 
    // cada vez que buscas un Usuario, mejorando el rendimiento.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "numero_documento", unique = true, length = 50)
    private String numeroDocumento;

    @Column(length = 150)
    private String carrera;
    private Integer ciclo;

    @Column(name = "codigo_estudiante")
    private String codigoEstudiante;

    @Column(name = "roles")
    private String roles;
    
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;
}
