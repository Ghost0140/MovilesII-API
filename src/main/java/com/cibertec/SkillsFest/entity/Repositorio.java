package com.cibertec.SkillsFest.entity;



import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "repositorios")
public class Repositorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false)
    private String url;

    private String plataforma;

    @Column(name = "total_commits")
    private Integer totalCommits;

    @Column(columnDefinition = "json")
    private String lenguajes;

    @Column(name = "ultimo_analisis")
    private Date ultimoAnalisis;
}
