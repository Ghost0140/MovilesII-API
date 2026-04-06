package com.cibertec.SkillsFest.serviceImpl;

import com.cibertec.SkillsFest.entity.PortafolioPublico;
import com.cibertec.SkillsFest.repository.IPortafolioPublicoRepository;
import com.cibertec.SkillsFest.service.IPortafolioPublicoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PortafolioPublicoServiceImpl implements IPortafolioPublicoService {

    private final IPortafolioPublicoRepository portfolioRepository;

    @Override
    public Optional<PortafolioPublico> obtenerPorId(Long id) {
        return portfolioRepository.findById(id)
                .filter(PortafolioPublico::getActivo);
    }

    @Override
    public Optional<PortafolioPublico> obtenerPorUsuario(Long usuarioId) {
        return portfolioRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public Optional<PortafolioPublico> obtenerPorSlug(String slug) {
        return portfolioRepository.findBySlugAndActivoTrue(slug);
    }

    @Override
    public PortafolioPublico crear(PortafolioPublico portfolio) {
        if (portfolio.getActivo() == null) {
            portfolio.setActivo(true);
        }
        return portfolioRepository.save(portfolio);
    }

    @Override
    public PortafolioPublico actualizar(Long id, PortafolioPublico portfolioActualizado) {
        PortafolioPublico portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        if (portfolioActualizado.getTitulo() != null) portfolio.setTitulo(portfolioActualizado.getTitulo());
        if (portfolioActualizado.getBio() != null) portfolio.setBio(portfolioActualizado.getBio());
        if (portfolioActualizado.getVisible() != null) portfolio.setVisible(portfolioActualizado.getVisible());
        if (portfolioActualizado.getSlug() != null) portfolio.setSlug(portfolioActualizado.getSlug());

        return portfolioRepository.save(portfolio);
    }

    @Override
    public void eliminar(Long id) {
        PortafolioPublico portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        portfolio.setActivo(false);
        portfolioRepository.save(portfolio);
    }
}