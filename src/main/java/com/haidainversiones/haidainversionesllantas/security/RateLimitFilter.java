package com.haidainversiones.haidainversionesllantas.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> loginBuckets    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> refreshBuckets  = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip   = obtenerIpCliente(request);

        if (path.equals("/api/auth/login")) {
            if (!intentarConsumir(loginBuckets, ip, 10)) {
                escribirErrorRateLimit(response, "Demasiados intentos de login. Espera 1 minuto.");
                return;
            }
        } else if (path.equals("/api/auth/register")) {
            if (!intentarConsumir(registerBuckets, ip, 5)) {
                escribirErrorRateLimit(response, "Demasiados intentos de registro. Espera 1 minuto.");
                return;
            }
        } else if (path.equals("/api/auth/refresh")) {
            if (!intentarConsumir(refreshBuckets, ip, 20)) {
                escribirErrorRateLimit(response, "Demasiadas solicitudes de refresh. Espera 1 minuto.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean intentarConsumir(ConcurrentHashMap<String, Bucket> buckets,
                                     String ip, int capacidad) {
        Bucket bucket = buckets.computeIfAbsent(ip, k -> crearBucket(capacidad));
        return bucket.tryConsume(1);
    }

    private Bucket crearBucket(int capacidadPorMinuto) {
        Bandwidth limite = Bandwidth.classic(
                capacidadPorMinuto,
                Refill.greedy(capacidadPorMinuto, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limite).build();
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void escribirErrorRateLimit(HttpServletResponse response, String mensaje)
            throws IOException {
        log.warn("Rate limit excedido: {}", mensaje);
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "60");
        String json = String.format("{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"%s\"}", mensaje);
        response.getWriter().write(json);
    }
}