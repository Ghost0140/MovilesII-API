package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    private String carrera;
    private Integer ciclo;

    @Column(name = "codigo_estudiante")
    private String codigoEstudiante;

    private String roles;
    private Boolean activo;

    @Column(name = "creado_en")
    private Date creadoEn;
}
