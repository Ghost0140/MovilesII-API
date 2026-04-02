package com.cibertec.SkillsFest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contribuciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"repositorio_id","usuario_id"}))
public class Contribucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repositorio_id", nullable = false)
    private Repositorio repositorio;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "total_commits")
    private Integer totalCommits;

    @Column(name = "total_lineas")
    private Integer totalLineas;

    private Double scoreFrontend;
    private Double scoreBackend;
    private Double scoreBd;
    private Double scoreMobile;
    private Double scoreTesting;

    @Column(columnDefinition = "json")
    private String tecnologiasDetectadas;

    @Column(name = "analizado_en")
    private Date analizadoEn;
}
