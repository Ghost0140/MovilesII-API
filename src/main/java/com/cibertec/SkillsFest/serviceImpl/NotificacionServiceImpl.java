package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.Notificacion;
import com.cibertec.SkillsFest.repository.INotificacionRepository;
import com.cibertec.SkillsFest.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final INotificacionRepository notificacionRepository;

    @Override
    public Notificacion enviar(Notificacion notificacion) {
        if (notificacion.getCreadoEn() == null) notificacion.setCreadoEn(new Date());
        if (notificacion.getLeida() == null) notificacion.setLeida(false);
        return notificacionRepository.save(notificacion);
    }

    @Override
    public Optional<Notificacion> obtenerPorId(Long id) {
        return notificacionRepository.findById(id);
    }

    @Override
    public List<Notificacion> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId);
    }

    @Override
    public List<Notificacion> listarNoLeidasPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByCreadoEnDesc(usuarioId);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(notif -> {
            notif.setLeida(true);
            notificacionRepository.save(notif);
        });
    }

    @Override
    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }
}