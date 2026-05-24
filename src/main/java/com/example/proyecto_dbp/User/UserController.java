// UserController.java
package com.example.proyecto_dbp.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Cualquier usuario autenticado ve su propio perfil
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMiPerfil() {
        return ResponseEntity.ok(userService.getMiPerfil());
    }

    // Ver perfil de otro usuario
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPorId(id));
    }

    // Actualizar mi perfil
    @PutMapping("/me")
    public ResponseEntity<UserResponse> actualizarPerfil(@RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.actualizarPerfil(request));
    }

    // Desactivar mi cuenta
    @DeleteMapping("/me")
    public ResponseEntity<Void> desactivarCuenta() {
        userService.desactivarCuenta();
        return ResponseEntity.noContent().build();
    }

    // Solo ADMIN puede ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getTodos() {
        return ResponseEntity.ok(userService.getTodos());
    }
}