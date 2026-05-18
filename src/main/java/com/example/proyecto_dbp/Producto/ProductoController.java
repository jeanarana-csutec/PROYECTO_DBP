package com.example.proyecto_dbp.Producto;


import com.example.proyecto_dbp.Cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CloudinaryService cloudinaryService;

    // Obtener todos los productos (Devuelve lista de DTOs)
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // Crear un nuevo producto recibiendo el RequestDTO
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody ProductoRequestDTO productoDTO) {
        ProductoResponseDTO nuevoProducto = productoService.crearProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // Actualizar un producto existente con RequestDTO
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO productoDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoDTO));
    }

    // Eliminar un producto (se mantiene igual ya que usa el ID de la URL)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Filtrado Adaptados ---

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.filtrarPorCategoria(categoriaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorEstado(@PathVariable EstadoProducto estado) {
        return ResponseEntity.ok(productoService.filtrarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorTipo(@PathVariable TipoProducto tipo) {
        return ResponseEntity.ok(productoService.filtrarPorTipo(tipo));
    }

    @PostMapping("/imagen")
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("file") MultipartFile file) {
        String urlImagen = cloudinaryService.subirImagen(file);

        Map<String, String> response = new HashMap<>();
        response.put("url", urlImagen);

        return ResponseEntity.ok(response);
    }
}