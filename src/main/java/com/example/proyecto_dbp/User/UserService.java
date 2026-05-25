// UserService.java
package com.example.proyecto_dbp.user;

import com.example.proyecto_dbp.exception.*;
import com.example.proyecto_dbp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtils securityUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public UserResponse getMiPerfil() {
        return modelMapper.map(securityUtils.getUsuarioAutenticado(), UserResponse.class);
    }

    @Transactional(readOnly = true)
    public UserResponse getPorId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    public UserResponse actualizarPerfil(UserUpdateRequest request) {
        User user = securityUtils.getUsuarioAutenticado();

        if (request.getNombre() != null) user.setNombre(request.getNombre());
        if (request.getFotoUrl() != null) user.setFotoUrl(request.getFotoUrl());
        if (request.getUniversidad() != null) user.setUniversidad(request.getUniversidad());

        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public void desactivarCuenta() {
        User user = securityUtils.getUsuarioAutenticado();
        user.setActivo(false);
        userRepository.save(user);
    }

    // Solo ADMIN
    @Transactional(readOnly = true)
    public Page<UserResponse> getTodos(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> modelMapper.map(u, UserResponse.class));
    }
}