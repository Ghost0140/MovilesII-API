package com.cibertec.SkillsFest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "portafolio_publico")
public class PortafolioPublico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    private Boolean visible;

    private String slug;

    private String titulo;
    private String bio;

    private Integer totalEventos;
    private Integer totalProyectos;
    private Integer premiosObtenidos;

    private Double radarFrontend;
    private Double radarBackend;
    private Double radarBd;
    private Double radarMobile;
    private Double radarTesting;

    @Column(name = "actualizado_en")
    private Date actualizadoEn;
}
