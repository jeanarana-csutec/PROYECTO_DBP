// ResenaController.java
package com.example.proyecto_dbp.Resena;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO request) {
        return new ResponseEntity<>(resenaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @GetMapping("/transaccion/{transaccionId}")
    public ResponseEntity<ResenaResponseDTO> obtenerPorTransaccion(@PathVariable Long transaccionId) {
        return ResponseEntity.ok(resenaService.obtenerPorTransaccion(transaccionId));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(resenaService.obtenerPorAutor(autorId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}