package com.cibertec.SkillsFest.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
//Lo que hace: Cuando conviertas esto a JSON, escóndeme esos objetos fantasma
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sedes")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(length = 100)
    private String distrito;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    private Boolean activo;
}
