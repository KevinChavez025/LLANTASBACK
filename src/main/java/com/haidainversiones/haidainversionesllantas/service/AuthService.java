package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.dto.AuthResponse;
import com.haidainversiones.haidainversionesllantas.dto.LoginRequest;
import com.haidainversiones.haidainversionesllantas.dto.RegisterRequest;
import com.haidainversiones.haidainversionesllantas.entity.Rol;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.repository.RolRepository;
import com.haidainversiones.haidainversionesllantas.repository.UsuarioRepository;
import com.haidainversiones.haidainversionesllantas.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rolNombre = usuario.getRoles().iterator().next().getNombre();

        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), usuario.getNombreCompleto(), rolNombre);
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setUsername(registerRequest.getEmail()); // ✅ AGREGAR ESTA LÍNEA
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setTelefono(registerRequest.getTelefono());
        usuario.setDireccion(registerRequest.getDireccion());
        usuario.setActivo(true);

        // Asignar rol USER por defecto
        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        usuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Auto login después del registro
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, usuarioGuardado.getId(), usuarioGuardado.getEmail(),
                usuarioGuardado.getNombreCompleto(), "USER");
    }
}
