package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "puntaje_jurados", precision = 10, scale = 2)
    private BigDecimal puntajeJurados;

    @Column(name = "puntaje_popular", precision = 10, scale = 2)
    private BigDecimal puntajePopular;

    @Column(name = "puntaje_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal puntajeTotal;

    private Integer posicion;

    @Column(name = "categoria_premio")
    private String categoriaPremio;

    @Column(nullable = false, length = 20)
    private String estado = "BORRADOR";

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}