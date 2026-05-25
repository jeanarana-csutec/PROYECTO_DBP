// ReviewController.java
package com.example.proyecto_dbp.Review;

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
public class ReviewController {

    private final ReviewService resenaService;

    @PostMapping
    public ResponseEntity<ReviewResponse> crear(@Valid @RequestBody ReviewRequest request) {
        return new ResponseEntity<>(resenaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @GetMapping("/transaccion/{transaccionId}")
    public ResponseEntity<ReviewResponse> obtenerPorTransaction(@PathVariable Long transaccionId) {
        return ResponseEntity.ok(resenaService.obtenerPorTransaction(transaccionId));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ReviewResponse>> obtenerPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(resenaService.obtenerPorAutor(autorId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRolee('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}