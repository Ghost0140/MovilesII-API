package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Equipo;
import com.cibertec.SkillsFest.entity.Notificacion;
import com.cibertec.SkillsFest.repository.IEquipoRepository;
import com.cibertec.SkillsFest.repository.INotificacionRepository;
import com.cibertec.SkillsFest.service.InvitacionEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitacionEquipoServiceImpl implements InvitacionEquipoService {

    private static final String TIPO_INVITACION = "EQUIPO_INVITACION:";
    private static final String TIPO_RECHAZADA = "EQUIPO_INVITACION_RECHAZADA:";

    private final INotificacionRepository notificacionRepository;
    private final IEquipoRepository equipoRepository;

    @Override
    @Transactional
    public int rechazarInvitacionesVencidas() {
        LocalDate hoy = LocalDate.now();

        int rechazadas = 0;
        for (Notificacion notificacion : notificacionRepository.findByTipoStartingWithAndActivoTrue(TIPO_INVITACION)) {
            Long equipoId = extraerEquipoId(notificacion.getTipo());
            if (equipoId == null) {
                continue;
            }

            Equipo equipo = equipoRepository.findById(equipoId).orElse(null);
            if (equipo == null || equipo.getEvento() == null || equipo.getEvento().getFechaFinInscripcion() == null) {
                continue;
            }

            if (hoy.isAfter(equipo.getEvento().getFechaFinInscripcion())) {
                notificacion.setTipo(TIPO_RECHAZADA + equipo.getId());
                notificacion.setTitulo("Invitación vencida");
                notificacion.setMensaje("La invitación al equipo " + equipo.getNombre()
                        + " fue rechazada automáticamente porque cerró la inscripción del evento "
                        + equipo.getEvento().getNombre() + ".");
                notificacion.setLeida(false);
                notificacion.setActivo(true);
                notificacionRepository.save(notificacion);
                rechazadas++;
            }
        }

        if (rechazadas > 0) {
            log.info("Invitaciones de equipo rechazadas automáticamente por cierre de inscripción: {}", rechazadas);
        }

        return rechazadas;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Lima")
    public void rechazarInvitacionesVencidasProgramado() {
        rechazarInvitacionesVencidas();
    }

    private Long extraerEquipoId(String tipo) {
        if (tipo == null || !tipo.startsWith(TIPO_INVITACION)) {
            return null;
        }

        try {
            return Long.parseLong(tipo.substring(TIPO_INVITACION.length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
