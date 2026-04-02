package com.cibertec.SkillsFest.entity;



import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "evaluaciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"proyecto_id","jurado_id","criterio_id"}))
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "jurado_id", nullable = false)
    private Usuario jurado;

    @ManyToOne
    @JoinColumn(name = "criterio_id", nullable = false)
    private CriterioEvaluacion criterio;

    @Column(nullable = false)
    private Double puntaje;

    private String comentario;

    @Column(name = "evaluado_en")
    private Date evaluadoEn;
}
