package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "repositorios")
public class Repositorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Proyecto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false)
    private String url;

    private String plataforma;

    @Column(name = "total_commits")
    private Integer totalCommits;

    // Guardamos los lenguajes como JSON (String)
    @Column(columnDefinition = "TEXT")
    private String lenguajes;

    // Usamos LocalDateTime (mejor que Date)
    @Column(name = "ultimo_analisis")
    private LocalDateTime ultimoAnalisis;
}