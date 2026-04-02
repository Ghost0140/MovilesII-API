package com.cibertec.SkillsFest.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ranking_sedes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id","sede_id"}))
public class RankingSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    private Integer posicion;

    @Column(name = "puntos_totales")
    private Double puntosTotales;

    @Column(name = "proyectos_presentados")
    private Integer proyectosPresentados;
}
