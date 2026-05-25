// TransaccionController.java
package com.example.proyecto_dbp.Transaccion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> misCompras(@PageableDefault(size = 20) Pageable pageable) {
        Page<TransaccionResponseDTO> page = transaccionService.misCompras(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-ventas")
    public ResponseEntity<Map<String, Object>> misVentas(@PageableDefault(size = 20) Pageable pageable) {
        Page<TransaccionResponseDTO> page = transaccionService.misVentas(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());
        return ResponseEntity.ok(response);
    }
}