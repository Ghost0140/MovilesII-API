-- =========================================
-- SKILLFEST - BD COMPLETA ACTUALIZADA
-- SOFT DELETE + MANEJO DE ESTADO
-- =========================================

DROP DATABASE IF EXISTS SkillFest;
CREATE DATABASE SkillFest
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE SkillFest;

-- =========================================
-- 1. SEDES
-- =========================================
CREATE TABLE sedes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    distrito VARCHAR(100),
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_codigo (codigo),
    INDEX idx_sede_activo (activo)
) ENGINE=InnoDB;

-- =========================================
-- 2. USUARIOS
-- =========================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sede_id BIGINT NOT NULL,

    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    numero_documento VARCHAR(50) UNIQUE,

    carrera VARCHAR(150),
    ciclo INT,
    codigo_estudiante VARCHAR(20) UNIQUE,
    github_username VARCHAR(100) UNIQUE,

    roles VARCHAR(255) DEFAULT 'ESTUDIANTE',
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_sede
        FOREIGN KEY (sede_id) REFERENCES sedes(id),

    CONSTRAINT chk_ciclo
        CHECK (ciclo BETWEEN 1 AND 12),

    INDEX idx_sede (sede_id),
    INDEX idx_email (email),
    INDEX idx_roles (roles),
    INDEX idx_github_username (github_username),
    INDEX idx_usuario_activo (activo)
) ENGINE=InnoDB;

-- =========================================
-- 3. EVENTOS
-- =========================================
CREATE TABLE eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sede_organizadora_id BIGINT NOT NULL,

    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    alcance VARCHAR(20) NOT NULL DEFAULT 'TODAS_SEDES',

    fecha_inicio_inscripcion DATE NOT NULL,
    fecha_fin_inscripcion DATE NOT NULL,
    fecha_evento DATE NOT NULL,

    permite_equipos BOOLEAN DEFAULT TRUE,
    max_miembros_equipo INT DEFAULT 5,
    permite_votacion_popular BOOLEAN DEFAULT FALSE,

    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',

    banner_url TEXT,
    creado_por BIGINT NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_evento_sede
        FOREIGN KEY (sede_organizadora_id) REFERENCES sedes(id),

    CONSTRAINT fk_evento_creador
        FOREIGN KEY (creado_por) REFERENCES usuarios(id),

    CONSTRAINT chk_tipo_evento
        CHECK (tipo IN ('FERIA','HACKATHON','CONCURSO','EXPOSICION')),

    CONSTRAINT chk_alcance
        CHECK (alcance IN ('SEDE','TODAS_SEDES','INTER_SEDES')),

    CONSTRAINT chk_estado_evento
        CHECK (estado IN ('BORRADOR','PUBLICADO','EN_CURSO','FINALIZADO','CANCELADO','ELIMINADO')),

    INDEX idx_evento_sede (sede_organizadora_id),
    INDEX idx_evento_estado (estado),
    INDEX idx_evento_fecha (fecha_evento)
) ENGINE=InnoDB;

-- =========================================
-- 4. EQUIPOS
-- =========================================
CREATE TABLE equipos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    sede_id BIGINT NOT NULL,

    nombre VARCHAR(150) NOT NULL,
    lider_id BIGINT NOT NULL,
    miembros JSON,
    asesor_id BIGINT,

    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipo_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    CONSTRAINT fk_equipo_sede
        FOREIGN KEY (sede_id) REFERENCES sedes(id),

    CONSTRAINT fk_equipo_lider
        FOREIGN KEY (lider_id) REFERENCES usuarios(id),

    CONSTRAINT fk_equipo_asesor
        FOREIGN KEY (asesor_id) REFERENCES usuarios(id),

    CONSTRAINT chk_estado_equipo
        CHECK (estado IN ('PENDIENTE','APROBADO','RECHAZADO','INACTIVO','ELIMINADO')),

    INDEX idx_equipo_evento (evento_id),
    INDEX idx_equipo_sede (sede_id),
    INDEX idx_equipo_lider (lider_id),
    INDEX idx_equipo_estado (estado)
) ENGINE=InnoDB;

-- =========================================
-- 5. PROYECTOS
-- =========================================
CREATE TABLE proyectos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    equipo_id BIGINT NULL,
    usuario_id BIGINT NULL,

    titulo VARCHAR(200) NOT NULL,
    resumen TEXT NOT NULL,
    descripcion TEXT,

    repositorio_url TEXT,
    video_url TEXT,
    demo_url TEXT,

    tecnologias JSON,
    estado VARCHAR(20) DEFAULT 'BORRADOR',

    fecha_envio TIMESTAMP NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_proyecto_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    CONSTRAINT fk_proyecto_equipo
        FOREIGN KEY (equipo_id) REFERENCES equipos(id),

    CONSTRAINT fk_proyecto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    CONSTRAINT chk_estado_proyecto
        CHECK (estado IN ('BORRADOR','ENVIADO','APROBADO','RECHAZADO','OBSERVADO','ELIMINADO')),

    INDEX idx_proyecto_evento (evento_id),
    INDEX idx_proyecto_estado (estado),
    INDEX idx_proyecto_equipo (equipo_id),
    INDEX idx_proyecto_usuario (usuario_id)
) ENGINE=InnoDB;

-- =========================================
-- 6. CRITERIOS EVALUACION
-- =========================================
CREATE TABLE criterios_evaluacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,

    nombre VARCHAR(100) NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    puntaje_maximo INT NOT NULL DEFAULT 10,

    CONSTRAINT fk_criterio_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    INDEX idx_criterio_evento (evento_id)
) ENGINE=InnoDB;

-- =========================================
-- 7. EVALUACIONES
-- =========================================
CREATE TABLE evaluaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,
    jurado_id BIGINT NOT NULL,
    criterio_id BIGINT NOT NULL,

    puntaje DECIMAL(5,2) NOT NULL,
    comentario TEXT,
    evaluado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_eval_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),

    CONSTRAINT fk_eval_jurado
        FOREIGN KEY (jurado_id) REFERENCES usuarios(id),

    CONSTRAINT fk_eval_criterio
        FOREIGN KEY (criterio_id) REFERENCES criterios_evaluacion(id),

    UNIQUE KEY unique_evaluacion (proyecto_id, jurado_id, criterio_id),
    INDEX idx_eval_proyecto (proyecto_id)
) ENGINE=InnoDB;

-- =========================================
-- 8. VOTOS POPULARES
-- =========================================
CREATE TABLE votos_populares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,
    usuario_id BIGINT NULL,
    ip_address VARCHAR(45),

    votado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_voto_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),

    CONSTRAINT fk_voto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    INDEX idx_voto_proyecto (proyecto_id)
) ENGINE=InnoDB;

-- =========================================
-- 9. RESULTADOS
-- =========================================
CREATE TABLE resultados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    proyecto_id BIGINT NOT NULL,

    puntaje_jurados DECIMAL(10,2) DEFAULT 0,
    puntaje_popular DECIMAL(10,2) DEFAULT 0,
    puntaje_total DECIMAL(10,2) NOT NULL,

    posicion INT,
    categoria_premio VARCHAR(50),

    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_publicacion TIMESTAMP NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_resultado_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    CONSTRAINT fk_resultado_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),

    CONSTRAINT chk_estado_resultado
        CHECK (estado IN ('BORRADOR','PUBLICADO','OCULTO','ELIMINADO')),

    UNIQUE KEY unique_evento_proyecto (evento_id, proyecto_id),
    INDEX idx_resultado_posicion (posicion),
    INDEX idx_resultado_estado (estado)
) ENGINE=InnoDB;

-- =========================================
-- 10. RANKING SEDES
-- =========================================
CREATE TABLE ranking_sedes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    sede_id BIGINT NOT NULL,

    posicion INT NOT NULL,
    puntos_totales DECIMAL(10,2) NOT NULL,
    proyectos_presentados INT DEFAULT 0,

    CONSTRAINT fk_rsede_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    CONSTRAINT fk_rsede_sede
        FOREIGN KEY (sede_id) REFERENCES sedes(id),

    UNIQUE KEY unique_evento_sede (evento_id, sede_id)
) ENGINE=InnoDB;

-- =========================================
-- 11. REPOSITORIOS
-- =========================================
CREATE TABLE repositorios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,

    url TEXT NOT NULL,
    plataforma VARCHAR(20) DEFAULT 'GITHUB',
    total_commits INT DEFAULT 0,
    lenguajes JSON,

    ultimo_analisis TIMESTAMP NULL,

    estado_analisis VARCHAR(30) DEFAULT 'PENDIENTE',
    detalle_error TEXT NULL,
    contributors_github INT DEFAULT 0,
    usuarios_mapeados INT DEFAULT 0,
    contribuciones_generadas INT DEFAULT 0,

    CONSTRAINT fk_repo_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),

    INDEX idx_repo_proyecto (proyecto_id),
    INDEX idx_repo_estado_analisis (estado_analisis)
) ENGINE=InnoDB;

-- =========================================
-- 12. CONTRIBUCIONES
-- =========================================
CREATE TABLE contribuciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repositorio_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    total_commits INT DEFAULT 0,
	total_lineas INT NULL,
		
    score_frontend DECIMAL(5,2) DEFAULT 0,
    score_backend DECIMAL(5,2) DEFAULT 0,
    score_bd DECIMAL(5,2) DEFAULT 0,
    score_mobile DECIMAL(5,2) DEFAULT 0,
    score_testing DECIMAL(5,2) DEFAULT 0,

    tecnologias_detectadas JSON,
    analizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contrib_repo
        FOREIGN KEY (repositorio_id) REFERENCES repositorios(id),

    CONSTRAINT fk_contrib_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    UNIQUE KEY unique_repo_usuario (repositorio_id, usuario_id),
    INDEX idx_contrib_usuario (usuario_id),
    INDEX idx_contrib_scores (score_frontend, score_backend)
) ENGINE=InnoDB;

-- =========================================
-- 13. RANKINGS AREA
-- =========================================
CREATE TABLE rankings_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    area VARCHAR(50) NOT NULL,
    score DECIMAL(10,2) NOT NULL,
    posicion INT NOT NULL,

    CONSTRAINT fk_ranking_evento
        FOREIGN KEY (evento_id) REFERENCES eventos(id),

    CONSTRAINT fk_ranking_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    CONSTRAINT chk_area
        CHECK (area IN ('FRONTEND','BACKEND','BD','MOBILE','TESTING','FULLSTACK')),

    UNIQUE KEY unique_evento_usuario_area (evento_id, usuario_id, area),
    INDEX idx_area_posicion (area, posicion)
) ENGINE=InnoDB;

-- =========================================
-- 14. PORTAFOLIO PUBLICO
-- =========================================
CREATE TABLE portafolio_publico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,

    visible BOOLEAN DEFAULT TRUE,
    activo BOOLEAN DEFAULT TRUE,
    slug VARCHAR(100) UNIQUE,

    titulo VARCHAR(200),
    bio TEXT,

    total_eventos INT DEFAULT 0,
    total_proyectos INT DEFAULT 0,
    premios_obtenidos INT DEFAULT 0,

    radar_frontend DECIMAL(5,2) DEFAULT 0,
    radar_backend DECIMAL(5,2) DEFAULT 0,
    radar_bd DECIMAL(5,2) DEFAULT 0,
    radar_mobile DECIMAL(5,2) DEFAULT 0,
    radar_testing DECIMAL(5,2) DEFAULT 0,

    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_portfolio_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    INDEX idx_portafolio_activo (activo)
) ENGINE=InnoDB;

-- =========================================
-- 15. NOTIFICACIONES
-- =========================================
CREATE TABLE notificaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,

    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,

    leida BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notif_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    INDEX idx_usuario_leida (usuario_id, leida),
    INDEX idx_notificacion_activo (activo)
) ENGINE=InnoDB;

-- =========================================
-- 16. COMENTARIOS
-- =========================================
CREATE TABLE comentarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    contenido TEXT NOT NULL,
    visible BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comentario_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),

    CONSTRAINT fk_comentario_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),

    INDEX idx_comentario_proyecto (proyecto_id),
    INDEX idx_comentario_visible (visible)
) ENGINE=InnoDB;

-- =========================================
-- DATOS DE PRUEBA
-- =========================================

INSERT INTO sedes (nombre, codigo, distrito, direccion, activo) VALUES
('CIBERTEC - Lima Norte', 'CIB-LN', 'Los Olivos', 'Av. Universitaria 1801', TRUE),
('CIBERTEC - Independencia', 'CIB-IND', 'Independencia', 'Av. Túpac Amaru 210', TRUE),
('CIBERTEC - San Juan de Lurigancho', 'CIB-SJL', 'San Juan de Lurigancho', 'Av. Próceres 1718', TRUE),
('CIBERTEC - Centro', 'CIB-CEN', 'Cercado', 'Jr. Cusco 140', TRUE);

INSERT INTO usuarios (
    sede_id, nombres, apellidos, email, password, numero_documento,
    carrera, ciclo, codigo_estudiante, github_username, roles, activo
) VALUES
(1, 'Juan Carlos', 'Rodríguez', 'admin.ln@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '12345678', NULL, NULL, NULL, NULL, 'ADMIN,ORGANIZADOR', TRUE),
(2, 'María Elena', 'García', 'admin.ind@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '23456789', NULL, NULL, NULL, NULL, 'ADMIN,ORGANIZADOR', TRUE),

(1, 'Luis Alberto', 'Sánchez', 'luis.sanchez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '34567890', 'Computación e Informática', NULL, NULL, NULL, 'PROFESOR,JURADO', TRUE),
(1, 'Carmen Rosa', 'Torres', 'carmen.torres@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '45678901', 'Diseño Gráfico', NULL, NULL, NULL, 'PROFESOR,JURADO', TRUE),
(2, 'Pedro José', 'Ramírez', 'pedro.ramirez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '56789012', 'Computación e Informática', NULL, NULL, NULL, 'PROFESOR,JURADO', TRUE),

(1, 'Erick Piero', 'Avendaño', 'erick.avendano@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '67890123', 'Computación e Informática', 6, 'u202212345', 'erickavendano', 'ESTUDIANTE', TRUE),
(1, 'Sofía Andrea', 'Silva', 'sofia.silva@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '78901234', 'Computación e Informática', 6, 'u202212346', 'sofiasilva', 'ESTUDIANTE', TRUE),
(1, 'Carlos Eduardo', 'Vargas', 'carlos.vargas@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '89012345', 'Computación e Informática', 5, 'u202212347', 'carlosvargas', 'ESTUDIANTE', TRUE),
(1, 'Lucía Fernanda', 'Ramírez', 'lucia.ramirez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '90123456', 'Diseño Gráfico', 4, 'u202212348', 'luciaramirez', 'ESTUDIANTE', TRUE),

(2, 'Diego Alonso', 'Martínez', 'diego.martinez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '01234567', 'Computación e Informática', 6, 'u202212349', 'diegomartinez', 'ESTUDIANTE', TRUE),
(2, 'Valentina Isabel', 'Gutiérrez', 'valentina.gutierrez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '11234567', 'Computación e Informática', 6, 'u202212350', 'valentinagutierrez', 'ESTUDIANTE', TRUE),
(2, 'Sebastián Andrés', 'López', 'sebastian.lopez@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '22234567', 'Redes y Comunicaciones', 5, 'u202212351', 'sebastianlopez', 'ESTUDIANTE', TRUE),

(3, 'Camila Andrea', 'Paredes', 'camila.paredes@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '33234567', 'Computación e Informática', 6, 'u202212352', 'camilaparedes', 'ESTUDIANTE', TRUE),
(3, 'Mateo José', 'Salazar', 'mateo.salazar@cibertec.edu.pe', '$2a$10$hud1/RjMomHJvGdCZyE6NuHunh8Wb71IfhfNGIRFGF1Ly8sQET5mO', '44234567', 'Computación e Informática', 5, 'u202212353', 'mateosalazar', 'ESTUDIANTE', TRUE);

INSERT INTO eventos (
    sede_organizadora_id, nombre, descripcion, tipo, alcance,
    fecha_inicio_inscripcion, fecha_fin_inscripcion, fecha_evento,
    permite_equipos, max_miembros_equipo, permite_votacion_popular,
    estado, creado_por
) VALUES
(1, 'CIBERTEC EXPO TECH 2026',
 'Feria de proyectos tecnológicos e innovadores',
 'FERIA', 'SEDE',
 '2026-03-01', '2026-04-20', '2026-05-15',
 TRUE, 5, TRUE,
 'PUBLICADO', 1),

(1, 'CIBERTEC Code Battle 2026',
 'Hackathon de 48 horas entre todas las sedes',
 'HACKATHON', 'INTER_SEDES',
 '2026-03-10', '2026-05-01', '2026-06-10',
 TRUE, 4, FALSE,
 'PUBLICADO', 1),

(2, 'CIBERTEC Startup Challenge',
 'Concurso de ideas de negocio',
 'CONCURSO', 'TODAS_SEDES',
 '2026-04-01', '2026-05-30', '2026-06-20',
 TRUE, 3, TRUE,
 'PUBLICADO', 2);

INSERT INTO criterios_evaluacion (evento_id, nombre, peso, puntaje_maximo) VALUES
(1, 'Innovación', 30.00, 10),
(1, 'Impacto', 25.00, 10),
(1, 'Viabilidad Técnica', 20.00, 10),
(1, 'Presentación', 15.00, 10),
(1, 'Prototipo', 10.00, 10),

(2, 'Creatividad', 25.00, 10),
(2, 'Código', 25.00, 10),
(2, 'Completitud', 20.00, 10),
(2, 'UX/UI', 15.00, 10),
(2, 'Pitch', 15.00, 10),

(3, 'Modelo de Negocio', 30.00, 10),
(3, 'Mercado', 25.00, 10),
(3, 'Escalabilidad', 20.00, 10),
(3, 'Equipo', 15.00, 10),
(3, 'Presentación', 10.00, 10);

INSERT INTO equipos (evento_id, sede_id, nombre, lider_id, miembros, asesor_id, estado) VALUES
(1, 1, 'CodeCrafters', 6, JSON_ARRAY(6, 7, 8), 3, 'APROBADO'),
(1, 1, 'InnoTech Solutions', 8, JSON_ARRAY(8, 9), 4, 'APROBADO'),
(2, 2, 'Digital Warriors', 10, JSON_ARRAY(10, 11, 12), 5, 'APROBADO'),
(2, 3, 'Tech Pioneers', 13, JSON_ARRAY(13, 14), 3, 'APROBADO');

INSERT INTO proyectos (
    evento_id, equipo_id, usuario_id, titulo, resumen, descripcion,
    repositorio_url, video_url, demo_url, tecnologias, estado, fecha_envio
) VALUES
(1, 1, NULL,
 'SmartPark - Estacionamiento Inteligente',
 'App móvil para encontrar espacios de estacionamiento en tiempo real',
 'Sistema que combina IoT con app móvil para optimizar estacionamiento',
 'https://github.com/erickavendano/smartpark',
 'https://youtube.com/watch?v=smartpark',
 'https://smartpark.demo.com',
 JSON_ARRAY('React Native', 'Spring Boot', 'Arduino', 'MySQL', 'Firebase'),
 'APROBADO', NOW()),

(1, 2, NULL,
 'EduAI - Asistente Virtual',
 'Chatbot educativo 24/7 para estudiantes',
 'Asistente virtual para resolver dudas académicas',
 'https://github.com/sofiasilva/eduai',
 'https://youtube.com/watch?v=eduai',
 'https://eduai.demo.com',
 JSON_ARRAY('Python', 'Flask', 'React', 'PostgreSQL'),
 'APROBADO', NOW()),

(2, 3, NULL,
 'GreenRoute - Rutas Ecológicas',
 'App de rutas con menor huella de carbono',
 'Sugiere rutas ecológicas combinando transporte público',
 'https://github.com/diegomartinez/greenroute',
 'https://youtube.com/watch?v=greenroute',
 'https://greenroute.demo.com',
 JSON_ARRAY('Swift', 'Node.js', 'MongoDB', 'Google Maps API'),
 'APROBADO', NOW()),

(2, 4, NULL,
 'MediTrack - Salud Personal',
 'Seguimiento de salud con recordatorios',
 'App para gestionar historial médico',
 'https://github.com/camilaparedes/meditrack',
 'https://youtube.com/watch?v=meditrack',
 'https://meditrack.demo.com',
 JSON_ARRAY('Kotlin', 'Spring Boot', 'MySQL', 'Firebase'),
 'APROBADO', NOW());

INSERT INTO evaluaciones (proyecto_id, jurado_id, criterio_id, puntaje, comentario) VALUES
(1, 3, 1, 9.00, 'Excelente innovación con IoT'),
(1, 3, 2, 8.50, 'Alto potencial de impacto'),
(1, 3, 3, 9.00, 'Implementación sólida'),
(1, 3, 4, 8.00, 'Buena presentación'),
(1, 3, 5, 9.50, 'Prototipo funcional'),

(1, 4, 1, 8.50, 'Muy innovador'),
(1, 4, 2, 9.00, 'Impacto social importante'),
(1, 4, 3, 8.00, 'Bien implementado'),
(1, 4, 4, 9.00, 'Excelente presentación visual'),
(1, 4, 5, 8.50, 'Demo convincente');

INSERT INTO votos_populares (proyecto_id, usuario_id, ip_address) VALUES
(1, 7, '192.168.1.100'),
(1, 8, '192.168.1.101'),
(1, 9, '192.168.1.102'),
(2, 6, '192.168.1.103'),
(2, 7, '192.168.1.104');

INSERT INTO repositorios (
    proyecto_id, url, plataforma, total_commits, lenguajes, ultimo_analisis,
    estado_analisis, detalle_error, contributors_github, usuarios_mapeados, contribuciones_generadas
) VALUES
(1, 'https://github.com/erickavendano/smartpark', 'GITHUB', 127,
 JSON_OBJECT('TypeScript', 45.2, 'Java', 30.5, 'CSS', 15.3, 'SQL', 9.0), NOW(),
 'COMPLETADO', NULL, 3, 3, 3),

(2, 'https://github.com/sofiasilva/eduai', 'GITHUB', 89,
 JSON_OBJECT('Python', 55.0, 'JavaScript', 35.0, 'HTML', 10.0), NOW(),
 'COMPLETADO', NULL, 1, 1, 1),

(3, 'https://github.com/diegomartinez/greenroute', 'GITHUB', 102,
 JSON_OBJECT('Swift', 50.0, 'JavaScript', 40.0, 'SQL', 10.0), NOW(),
 'COMPLETADO', NULL, 1, 1, 1),

(4, 'https://github.com/camilaparedes/meditrack', 'GITHUB', 95,
 JSON_OBJECT('Kotlin', 60.0, 'Java', 30.0, 'SQL', 10.0), NOW(),
 'COMPLETADO', NULL, 1, 1, 1);

INSERT INTO contribuciones (
    repositorio_id, usuario_id, total_commits, total_lineas,
    score_frontend, score_backend, score_bd, score_mobile, score_testing,
    tecnologias_detectadas, analizado_en
) VALUES
(1, 6, 65, 4523, 85.00, 92.00, 78.00, 88.00, 65.00,
 JSON_ARRAY(
     JSON_OBJECT('nombre', 'TypeScript', 'nivel', 'AVANZADO', 'porcentaje', 45.2),
     JSON_OBJECT('nombre', 'Spring Boot', 'nivel', 'AVANZADO', 'porcentaje', 30.5),
     JSON_OBJECT('nombre', 'React Native', 'nivel', 'AVANZADO', 'porcentaje', 20.0)
 ), NOW()),

(1, 7, 45, 3200, 95.00, 40.00, 30.00, 92.00, 88.00,
 JSON_ARRAY(
     JSON_OBJECT('nombre', 'React Native', 'nivel', 'EXPERTO', 'porcentaje', 50.0),
     JSON_OBJECT('nombre', 'CSS', 'nivel', 'AVANZADO', 'porcentaje', 25.0),
     JSON_OBJECT('nombre', 'Jest', 'nivel', 'AVANZADO', 'porcentaje', 15.0)
 ), NOW()),

(1, 8, 17, 1800, 30.00, 88.00, 85.00, 25.00, 55.00,
 JSON_ARRAY(
     JSON_OBJECT('nombre', 'Spring Boot', 'nivel', 'AVANZADO', 'porcentaje', 55.0),
     JSON_OBJECT('nombre', 'MySQL', 'nivel', 'AVANZADO', 'porcentaje', 25.0)
 ), NOW());

INSERT INTO rankings_area (evento_id, usuario_id, area, score, posicion) VALUES
(1, 7, 'FRONTEND', 95.00, 1),
(1, 6, 'FRONTEND', 85.00, 2),

(1, 6, 'BACKEND', 92.00, 1),
(1, 8, 'BACKEND', 88.00, 2),

(1, 8, 'BD', 85.00, 1),
(1, 6, 'BD', 78.00, 2),

(1, 7, 'TESTING', 88.00, 1),
(1, 8, 'TESTING', 55.00, 2);

INSERT INTO portafolio_publico (
    usuario_id, visible, activo, slug, titulo, bio,
    total_eventos, total_proyectos, premios_obtenidos,
    radar_frontend, radar_backend, radar_bd, radar_mobile, radar_testing
) VALUES
(6, TRUE, TRUE, 'erick-avendano', 'Full Stack Developer',
 'Estudiante de Computación e Informática especializado en desarrollo full stack',
 1, 1, 0, 85.00, 92.00, 78.00, 88.00, 65.00),

(7, TRUE, TRUE, 'sofia-silva', 'Frontend & Mobile Specialist',
 'Desarrolladora frontend especializada en React Native y testing',
 1, 1, 0, 95.00, 40.00, 30.00, 92.00, 88.00);

INSERT INTO notificaciones (usuario_id, tipo, titulo, mensaje, leida, activo) VALUES
(6, 'EVENTO_NUEVO', 'Nuevo evento publicado', 'CIBERTEC EXPO TECH 2026 está abierto a inscripciones', FALSE, TRUE),
(7, 'EQUIPO_INVITACION', 'Invitación a equipo', 'Erick Avendaño te ha invitado a unirte a CodeCrafters', FALSE, TRUE),
(6, 'PROYECTO_APROBADO', 'Proyecto aprobado', 'Tu proyecto SmartPark ha sido aprobado por los organizadores', FALSE, TRUE);

INSERT INTO comentarios (proyecto_id, usuario_id, contenido, visible) VALUES
(1, 7, 'Excelente proyecto, la integración con IoT es muy innovadora', TRUE),
(1, 9, 'Me encantó la demo, muy bien ejecutado', TRUE),
(2, 6, 'Gran idea, podría ayudar a muchos estudiantes', TRUE),
(3, 14, 'Muy necesario para nuestra ciudad', TRUE);

INSERT INTO resultados (
    evento_id, proyecto_id, puntaje_jurados, puntaje_popular, puntaje_total,
    posicion, categoria_premio, estado, fecha_publicacion
) VALUES
(1, 1, 8.70, 3.00, 11.70, 1, 'ORO', 'PUBLICADO', NOW()),
(1, 2, 0.00, 2.00, 2.00, 2, 'PLATA', 'PUBLICADO', NOW());

INSERT INTO ranking_sedes (evento_id, sede_id, posicion, puntos_totales, proyectos_presentados) VALUES
(1, 1, 1, 120.00, 2),
(1, 2, 2, 80.00, 1),
(1, 3, 3, 50.00, 1);


-- verifiquen que la columna password sea lo suficientemente larga para guardar el hash encriptado (mínimo 60 caracteres).
ALTER TABLE usuarios MODIFY password VARCHAR(255);



USE SkillFest;

UPDATE usuarios
SET github_username = 'Ghost0140'
WHERE email = 'erick.avendano@cibertec.edu.pe';

SELECT id, email, github_username
FROM usuarios
WHERE email = 'erick.avendano@cibertec.edu.pe';

USE SkillFest;

INSERT INTO proyectos (
    evento_id,
    equipo_id,
    usuario_id,
    titulo,
    resumen,
    descripcion,
    repositorio_url,
    video_url,
    demo_url,
    tecnologias,
    estado,
    fecha_envio
)
VALUES (
    1,
    NULL,
    6,
    'MovilesII Front Real',
    'Proyecto real para validar Talent Radar',
    'Proyecto frontend real desarrollado con React y Vite para pruebas del Radar',
    'https://github.com/Ghost0140/MovilesII-FRONT.git',
    NULL,
    NULL,
    JSON_ARRAY('React', 'Vite', 'JavaScript'),
    'ENVIADO',
    NOW()
);

SELECT LAST_INSERT_ID() AS proyecto_id;

