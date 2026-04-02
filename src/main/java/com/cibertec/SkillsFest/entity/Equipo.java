package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "lider_id", nullable = false)
    private Usuario lider;

    @Column(columnDefinition = "json")
    private String miembros;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    private Boolean aprobado;

    @Column(name = "creado_en")
    private Date creadoEn;
}