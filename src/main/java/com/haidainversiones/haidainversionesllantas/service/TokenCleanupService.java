package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Limpieza automática de refresh tokens expirados.
 * Evita que la tabla crezca indefinidamente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    /** Se ejecuta cada día a las 3:00 AM */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void limpiarTokensExpirados() {
        refreshTokenRepository.eliminarExpirados();
        log.info("Limpieza de refresh tokens expirados completada");
    }
}
