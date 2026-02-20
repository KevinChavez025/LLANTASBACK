package com.haidainversiones.haidainversionesllantas.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché en memoria (Caffeine/ConcurrentMap).
 * Para producción con múltiples instancias, reemplazar por Redis:
 *   spring.cache.type=redis
 *   spring.data.redis.host=${REDIS_HOST:localhost}
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("productos");
    }
}
