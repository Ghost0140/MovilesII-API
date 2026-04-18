package com.cibertec.SkillsFest.config;

import com.cibertec.SkillsFest.entity.Sede; 
import com.cibertec.SkillsFest.entity.Usuario;
import com.cibertec.SkillsFest.repository.IUsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

public class DataInitializer {

	@Bean
    public CommandLineRunner initDatabase(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                System.out.println("🌱 [SEED] Base de datos vacía. Iniciando carga de usuarios con BCrypt...");

                List<Usuario> usuariosIniciales = Arrays.asList(
                    crearUsuario(1L, "Juan Carlos", "Rodríguez", "admin.ln@cibertec.edu.pe", "123456", "12345678", null, null, null, null, "ADMIN,ORGANIZADOR", passwordEncoder),
                    crearUsuario(2L, "María Elena", "García", "admin.ind@cibertec.edu.pe", "123456", "23456789", null, null, null, null, "ADMIN,ORGANIZADOR", passwordEncoder),
                    crearUsuario(1L, "Luis Alberto", "Sánchez", "luis.sanchez@cibertec.edu.pe", "123456", "34567890", "Computación e Informática", null, null, null, "PROFESOR,JURADO", passwordEncoder),
                    crearUsuario(1L, "Carmen Rosa", "Torres", "carmen.torres@cibertec.edu.pe", "123456", "45678901", "Diseño Gráfico", null, null, null, "PROFESOR,JURADO", passwordEncoder),
                    crearUsuario(2L, "Pedro José", "Ramírez", "pedro.ramirez@cibertec.edu.pe", "123456", "56789012", "Computación e Informática", null, null, null, "PROFESOR,JURADO", passwordEncoder),
                    crearUsuario(1L, "Erick Piero", "Avendaño", "erick.avendano@cibertec.edu.pe", "123456", "67890123", "Computación e Informática", 6, "u202212345", "erickavendano", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(1L, "Sofía Andrea", "Silva", "sofia.silva@cibertec.edu.pe", "123456", "78901234", "Computación e Informática", 6, "u202212346", "sofiasilva", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(1L, "Carlos Eduardo", "Vargas", "carlos.vargas@cibertec.edu.pe", "123456", "89012345", "Computación e Informática", 5, "u202212347", "carlosvargas", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(1L, "Lucía Fernanda", "Ramírez", "lucia.ramirez@cibertec.edu.pe", "123456", "90123456", "Diseño Gráfico", 4, "u202212348", "luciaramirez", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(2L, "Diego Alonso", "Martínez", "diego.martinez@cibertec.edu.pe", "123456", "01234567", "Computación e Informática", 6, "u202212349", "diegomartinez", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(2L, "Valentina Isabel", "Gutiérrez", "valentina.gutierrez@cibertec.edu.pe", "123456", "11234567", "Computación e Informática", 6, "u202212350", "valentinagutierrez", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(2L, "Sebastián Andrés", "López", "sebastian.lopez@cibertec.edu.pe", "123456", "22234567", "Redes y Comunicaciones", 5, "u202212351", "sebastianlopez", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(3L, "Camila Andrea", "Paredes", "camila.paredes@cibertec.edu.pe", "123456", "33234567", "Computación e Informática", 6, "u202212352", "camilaparedes", "ESTUDIANTE", passwordEncoder),
                    crearUsuario(3L, "Mateo José", "Salazar", "mateo.salazar@cibertec.edu.pe", "123456", "44234567", "Computación e Informática", 5, "u202212353", "mateosalazar", "ESTUDIANTE", passwordEncoder)
                );

                usuarioRepository.saveAll(usuariosIniciales);
                System.out.println("✅ [SEED] 14 usuarios creados exitosamente. Contraseñas seguras con BCrypt.");
            } else {
                System.out.println("⚡ [SEED] La base de datos ya contiene usuarios. Omitiendo inicialización.");
            }
        };
    }


    private Usuario crearUsuario(Long sedeId, String nombres, String apellidos, String email, String passwordPlana,
                                 String numeroDocumento, String carrera, Integer ciclo, String codigoEstudiante,
                                 String githubUsername, String roles, PasswordEncoder encoder) {
        
        Usuario usuario = new Usuario();
       
        Sede sede = new Sede();
        sede.setId(sedeId);
        usuario.setSede(sede);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(encoder.encode(passwordPlana));
        usuario.setNumeroDocumento(numeroDocumento);
        usuario.setCarrera(carrera);
        usuario.setCiclo(ciclo);
        usuario.setCodigoEstudiante(codigoEstudiante);
        usuario.setGithubUsername(githubUsername);
        usuario.setRoles(roles);
        usuario.setActivo(true);

        return usuario;
    }
}
