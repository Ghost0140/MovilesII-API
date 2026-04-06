package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String tipo;
    private String titulo;
    private String mensaje;

    private Boolean leida = false;
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}