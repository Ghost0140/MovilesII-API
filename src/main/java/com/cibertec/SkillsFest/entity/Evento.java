package com.cibertec.SkillsFest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_organizadora_id", nullable = false)
    private Sede sedeOrganizadora;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String alcance = "TODAS_SEDES";

    @Column(name = "fecha_inicio_inscripcion", nullable = false)
    private LocalDate fechaInicioInscripcion;

    @Column(name = "fecha_fin_inscripcion", nullable = false)
    private LocalDate fechaFinInscripcion;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "permite_equipos")
    private Boolean permiteEquipos = true;

    private Integer maxMiembrosEquipo;

    private Boolean permiteVotacionPopular = false;

    @Column(nullable = false, length = 20)
    private String estado = "BORRADOR";

    @Column(name = "banner_url", columnDefinition = "TEXT")
    private String bannerUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}