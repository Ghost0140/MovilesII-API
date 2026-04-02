package com.cibertec.SkillsFest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String resumen;

    private String descripcion;

    @Column(name = "repositorio_url")
    private String repositorioUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "demo_url")
    private String demoUrl;

    @Column(columnDefinition = "json")
    private String tecnologias;

    private String estado;

    @Column(name = "fecha_envio")
    private Date fechaEnvio;

    @Column(name = "creado_en")
    private Date creadoEn;
}