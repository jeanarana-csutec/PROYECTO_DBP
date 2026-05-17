package com.example.proyecto_dbp.Favorito;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    // Agregar un producto a la lista de favoritos
    @PostMapping
    public ResponseEntity<FavoritoResponseDTO> agregarFavorito(@RequestBody FavoritoRequestDTO dto) {
        FavoritoResponseDTO nuevoFavorito = favoritoService.agregarFavorito(dto);
        return new ResponseEntity<>(nuevoFavorito, HttpStatus.CREATED);
    }

    // Obtener todos los productos favoritos de un usuario concreto
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FavoritoResponseDTO>> obtenerFavoritosDeUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(favoritoService.obtenerFavoritosPorUsuario(usuarioId));
    }

    // Eliminar un producto de la lista de favoritos de un usuario
    @DeleteMapping("/usuario/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Void> eliminarFavorito(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId) {
        favoritoService.eliminarFavorito(usuarioId, productoId);
        return ResponseEntity.noContent().build();
    }
}