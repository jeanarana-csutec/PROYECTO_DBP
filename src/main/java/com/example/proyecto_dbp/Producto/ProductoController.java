package com.example.proyecto_dbp.Producto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
        return new ResponseEntity<>(productoService.crearProducto(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> porCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.filtrarPorCategoria(categoriaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProductoResponseDTO>> porEstado(@PathVariable EstadoProducto estado) {
        return ResponseEntity.ok(productoService.filtrarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ProductoResponseDTO>> porTipo(@PathVariable TipoProducto tipo) {
        return ResponseEntity.ok(productoService.filtrarPorTipo(tipo));
    }
}