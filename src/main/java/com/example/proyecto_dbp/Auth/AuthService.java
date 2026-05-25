package com.example.proyecto_dbp.auth;

import com.example.proyecto_dbp.event.UserRegisteredEvent;
import com.example.proyecto_dbp.exception.InvalidOperationException;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
import com.example.proyecto_dbp.exception.UserAlreadyExistsException;
import com.example.proyecto_dbp.security.JwtService;
import com.example.proyecto_dbp.user.Role;
import com.example.proyecto_dbp.user.User;
import com.example.proyecto_dbp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    public RegisterResponse registro(RegisterRequest rq) {
        if (userRepository.existsByEmail(rq.getEmail())) {
            throw new UserAlreadyExistsException("Este usuario ya existe");
        }

        User user = new User();
        user.setNombre(generarUsername(rq.getEmail()));
        user.setEmail(rq.getEmail());
        user.setPassword(passwordEncoder.encode(rq.getPassword()));
        user.setRole(Role.USER);
        user.setFechaRegistro(LocalDateTime.now());
        user.setUniversidad(obtenerUniversidad(rq.getEmail()));

        user = userRepository.save(user);
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(this, user));

        RegisterResponse response = modelMapper.map(user, RegisterResponse.class);
        response.setToken(jwtService.generateToken(user));
        response.setRefreshToken(jwtService.generateRefreshToken(user));
        return response;
    }

    public LoginResponse login(LoginRequest rq) {
        User user = userRepository.findByEmail(rq.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario"));

        if (!passwordEncoder.matches(rq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password incorrecta");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponse(accessToken, refreshToken);
    }

    public String obtenerUniversidad(String email) {
        String dominio = email.split("@")[1];
        return dominio.split("\\.")[0];
    }

    public String generarUsername(String email) {
        String parte = email.split("@")[0];
        String[] datos = parte.split("\\.");

        if (datos.length >= 2) {
            return capitalize(datos[0]) + " " + capitalize(datos[1]);
        }
        return capitalize(datos[0]);
    }

    public LoginResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidOperationException("Token de refresco invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido o expirado");
        }
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}