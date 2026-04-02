package com.cibertec.SkillsFest.entity;



import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resultados")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(name = "puntaje_jurados")
    private Double puntajeJurados;

    @Column(name = "puntaje_popular")
    private Double puntajePopular;

    @Column(name = "puntaje_total", nullable = false)
    private Double puntajeTotal;

    private Integer posicion;

    @Column(name = "categoria_premio")
    private String categoriaPremio;

    private Boolean publicado;

    @Column(name = "fecha_publicacion")
    private Date fechaPublicacion;
}
