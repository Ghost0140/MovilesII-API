package com.cibertec.SkillsFest.entity;


import jakarta.persistence.*;
import lombok.*;

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

    private Double score;

    private Integer posicion;
}
