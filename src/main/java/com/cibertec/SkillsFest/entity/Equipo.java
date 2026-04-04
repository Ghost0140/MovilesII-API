package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
//Lo que hace: Cuando conviertas esto a JSON, escóndeme esos objetos fantasma
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @Column(nullable = false, length = 150)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lider_id", nullable = false)
    private Usuario lider;

    @Column(columnDefinition = "json")
    private String miembros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    private Boolean aprobado = false;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDate creadoEn;
}