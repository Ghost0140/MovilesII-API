package com.cibertec.SkillsFest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "repositorios")
public class Repositorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Proyecto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false)
    private String url;

    private String plataforma;

    @Column(name = "total_commits")
    private Integer totalCommits;

    // Guardamos los lenguajes como JSON String
    @Column(columnDefinition = "TEXT")
    private String lenguajes;

    @Column(name = "ultimo_analisis")
    private LocalDateTime ultimoAnalisis;

    // ==============================
    // NUEVOS CAMPOS PARA EL RADAR
    // ==============================

    @Column(name = "estado_analisis", length = 30)
    private String estadoAnalisis;

    @Column(name = "detalle_error", columnDefinition = "TEXT")
    private String detalleError;

    @Column(name = "contributors_github")
    private Integer contributorsGithub;

    @Column(name = "usuarios_mapeados")
    private Integer usuariosMapeados;

    @Column(name = "contribuciones_generadas")
    private Integer contribucionesGeneradas;

    @PrePersist
    public void prePersist() {
        if (plataforma == null) {
            plataforma = "GITHUB";
        }

        if (estadoAnalisis == null) {
            estadoAnalisis = "PENDIENTE";
        }

        if (contributorsGithub == null) {
            contributorsGithub = 0;
        }

        if (usuariosMapeados == null) {
            usuariosMapeados = 0;
        }

        if (contribucionesGeneradas == null) {
            contribucionesGeneradas = 0;
        }
    }
}