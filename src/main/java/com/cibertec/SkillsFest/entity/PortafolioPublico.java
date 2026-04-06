package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private Boolean visible = true;
    private Boolean activo = true;

    private String slug;
    private String titulo;
    private String bio;

    private Integer totalEventos;
    private Integer totalProyectos;
    private Integer premiosObtenidos;

    @Column(name = "radar_frontend", precision = 5, scale = 2)
    private BigDecimal radarFrontend;

    @Column(name = "radar_backend", precision = 5, scale = 2)
    private BigDecimal radarBackend;

    @Column(name = "radar_bd", precision = 5, scale = 2)
    private BigDecimal radarBd;

    @Column(name = "radar_mobile", precision = 5, scale = 2)
    private BigDecimal radarMobile;

    @Column(name = "radar_testing", precision = 5, scale = 2)
    private BigDecimal radarTesting;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}