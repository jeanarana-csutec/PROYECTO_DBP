package com.example.proyecto_dbp.Auth;

import com.example.proyecto_dbp.Events.UsuarioRegistradoEvent;
import com.example.proyecto_dbp.Exceptions.UserAlreadyExists;
import com.example.proyecto_dbp.Security.JwtService;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
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

    public AuthResponse registro(AuthRequest rq) {
        if (userRepository.existsByEmail(rq.getEmail())) {
            throw new UserAlreadyExists("Este usuario ya existe");
        }

        User user = new User();
        user.setNombre(generarUsername(rq.getEmail()));
        user.setEmail(rq.getEmail());
        user.setPassword(passwordEncoder.encode(rq.getPassword()));
        user.setRol(Rol.USER);
        user.setFechaRegistro(LocalDateTime.now());
        user.setUniversidad(obtenerUniversidad(rq.getEmail()));

        user = userRepository.save(user);
        applicationEventPublisher.publishEvent(new UsuarioRegistradoEvent(this, user));

        AuthResponse response = modelMapper.map(user, AuthResponse.class);
        response.setToken(jwtService.generateToken(user));
        return response;
    }

    public AuthLoginResponse login(AuthLoginRequest rq) {
        User user = userRepository.findByEmail(rq.getEmail())
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        if (!passwordEncoder.matches(rq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password incorrecta");
        }

        return new AuthLoginResponse(jwtService.generateToken(user));
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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}