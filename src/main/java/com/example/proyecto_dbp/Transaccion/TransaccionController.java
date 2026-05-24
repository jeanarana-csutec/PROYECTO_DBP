// TransaccionController.java
package com.example.proyecto_dbp.Transaccion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> crear(@Valid @RequestBody TransaccionRequestDTO request) {
        return new ResponseEntity<>(transaccionService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<TransaccionResponseDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.completar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TransaccionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.cancelar(id));
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<TransaccionResponseDTO>> misCompras() {
        return ResponseEntity.ok(transaccionService.misCompras());
    }

    @GetMapping("/mis-ventas")
    public ResponseEntity<List<TransaccionResponseDTO>> misVentas() {
        return ResponseEntity.ok(transaccionService.misVentas());
    }
}