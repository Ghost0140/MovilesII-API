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
@Table(
        name = "contribuciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"repositorio_id", "usuario_id"})
)
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

    @Column(name = "score_frontend", precision = 5, scale = 2)
    private BigDecimal scoreFrontend;

    @Column(name = "score_backend", precision = 5, scale = 2)
    private BigDecimal scoreBackend;

    @Column(name = "score_bd", precision = 5, scale = 2)
    private BigDecimal scoreBd;

    @Column(name = "score_mobile", precision = 5, scale = 2)
    private BigDecimal scoreMobile;

    @Column(name = "score_testing", precision = 5, scale = 2)
    private BigDecimal scoreTesting;

    @Column(name = "tecnologias_detectadas", columnDefinition = "json")
    private String tecnologiasDetectadas;

    @Column(name = "analizado_en")
    private LocalDateTime analizadoEn;
}