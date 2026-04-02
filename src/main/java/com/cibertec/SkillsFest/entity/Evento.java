package com.cibertec.SkillsFest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sede_organizadora_id", nullable = false)
    private Sede sedeOrganizadora;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String alcance;

    @Column(name = "fecha_inicio_inscripcion", nullable = false)
    private Date fechaInicioInscripcion;

    @Column(name = "fecha_fin_inscripcion", nullable = false)
    private Date fechaFinInscripcion;

    @Column(name = "fecha_evento", nullable = false)
    private Date fechaEvento;

    private Boolean permiteEquipos;
    private Integer maxMiembrosEquipo;
    private Boolean permiteVotacionPopular;

    @Column(nullable = false)
    private String estado;

    @Column(name = "banner_url")
    private String bannerUrl;

    @ManyToOne
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en")
    private Date creadoEn;
}
