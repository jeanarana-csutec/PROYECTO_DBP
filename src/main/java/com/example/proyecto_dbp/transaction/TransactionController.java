// TransactionController.java
package com.example.proyecto_dbp.Transaction;

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
public class TransactionController {

    private final TransactionService transaccionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> crear(@Valid @RequestBody TransactionRequest request) {
        return new ResponseEntity<>(transaccionService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<TransactionResponse> completar(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.completar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TransactionResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.cancelar(id));
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<Map<String, Object>> misCompras(@PageableDefault(size = 20) Pageable pageable) {
        Page<TransactionResponse> page = transaccionService.misCompras(pageable);
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
        Page<TransactionResponse> page = transaccionService.misVentas(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());
        return ResponseEntity.ok(response);
    }
}