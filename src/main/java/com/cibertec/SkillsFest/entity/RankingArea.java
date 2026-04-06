package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rankings_area",
        uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id","usuario_id","area"}))
public class RankingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String area;

    @Column(precision = 10, scale = 2)
    private BigDecimal score;

    private Integer posicion;
}