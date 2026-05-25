// FavoriteController.java
package com.example.proyecto_dbp.Favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoritoService;

    @PostMapping("/{productoId}")
    public ResponseEntity<FavoriteResponse> agregar(@PathVariable Long productoId) {
        return new ResponseEntity<>(favoritoService.agregar(productoId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> misFavorites() {
        return ResponseEntity.ok(favoritoService.misFavorites());
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        favoritoService.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}