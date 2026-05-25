package com.example.proyecto_dbp.Product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodos(@PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> page = productoService.obtenerTodos(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("number", page.getNumber());
        response.put("size", page.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> crear(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productoService.crearProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productoService.actualizarProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRolee('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductResponse>> porCategory(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.filtrarPorCategory(categoriaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProductResponse>> porEstado(@PathVariable ProductStatus estado) {
        return ResponseEntity.ok(productoService.filtrarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ProductResponse>> porTipo(@PathVariable ProductType tipo) {
        return ResponseEntity.ok(productoService.filtrarPorTipo(tipo));
    }
}