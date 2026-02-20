package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.dto.AuthResponse;
import com.haidainversiones.haidainversionesllantas.dto.LoginRequest;
import com.haidainversiones.haidainversionesllantas.dto.RegisterRequest;
import com.haidainversiones.haidainversionesllantas.entity.RefreshToken;
import com.haidainversiones.haidainversionesllantas.entity.Rol;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.exception.UnauthorizedException;
import com.haidainversiones.haidainversionesllantas.repository.RefreshTokenRepository;
import com.haidainversiones.haidainversionesllantas.repository.RolRepository;
import com.haidainversiones.haidainversionesllantas.repository.UsuarioRepository;
import com.haidainversiones.haidainversionesllantas.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** Refresh token: 7 días por defecto */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", loginRequest.getEmail()));

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String accessToken  = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = crearRefreshToken(usuario);

        String rolNombre = usuario.getRoles().stream()
                .findFirst()
                .map(Rol::getNombre)
                .orElse("USER");

        return new AuthResponse(accessToken, refreshToken,
                usuario.getId(), usuario.getEmail(), usuario.getNombreCompleto(), rolNombre);
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(registerRequest.getEmail())) {
            throw new BadRequestException("El username ya está en uso");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setUsername(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setTelefono(registerRequest.getTelefono());
        usuario.setDireccion(registerRequest.getDireccion());
        usuario.setActivo(true);

        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol USER no encontrado. Ejecuta las migraciones Flyway."));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        usuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        String accessToken  = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = crearRefreshToken(usuarioGuardado);

        return new AuthResponse(accessToken, refreshToken,
                usuarioGuardado.getId(), usuarioGuardado.getEmail(),
                usuarioGuardado.getNombreCompleto(), "USER");
    }

    /**
     * Renueva el access token usando un refresh token válido.
     * El refresh token se rota (invalida el anterior y emite uno nuevo).
     */
    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken rt = refreshTokenRepository
                .findByTokenAndRevocadoFalse(refreshTokenStr)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido o revocado."));

        if (!rt.estaVigente()) {
            // Expirado — revocar y forzar re-login
            rt.setRevocado(true);
            refreshTokenRepository.save(rt);
            throw new UnauthorizedException("Refresh token expirado. Por favor inicia sesión de nuevo.");
        }

        Usuario usuario = rt.getUsuario();

        // Rotación: revocar el token usado y emitir uno nuevo
        rt.setRevocado(true);
        refreshTokenRepository.save(rt);

        String nuevoAccessToken  = jwtTokenProvider.generateAccessTokenFromEmail(usuario.getEmail());
        String nuevoRefreshToken = crearRefreshToken(usuario);

        return new AuthResponse(nuevoAccessToken, nuevoRefreshToken);
    }

    /**
     * Logout: revoca todos los refresh tokens del usuario.
     * El access token expirará solo (es de corta vida).
     */
    @Transactional
    public void logout(Long usuarioId) {
        refreshTokenRepository.revocarTodosPorUsuario(usuarioId);
    }

    private String crearRefreshToken(Usuario usuario) {
        RefreshToken rt = new RefreshToken();
        rt.setUsuario(usuario);
        rt.setToken(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
        rt.setFechaExpira(LocalDateTime.now()
                .plusSeconds(refreshExpirationMs / 1000));
        rt.setRevocado(false);
        refreshTokenRepository.save(rt);
        return rt.getToken();
    }
}
