// FavoritoController.java
package com.example.proyecto_dbp.Favorito;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping("/{productoId}")
    public ResponseEntity<FavoritoResponseDTO> agregar(@PathVariable Long productoId) {
        return new ResponseEntity<>(favoritoService.agregar(productoId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FavoritoResponseDTO>> misFavoritos() {
        return ResponseEntity.ok(favoritoService.misFavoritos());
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        favoritoService.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}