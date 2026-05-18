package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Categoria.CategoriaRepository;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository; // Ajusta el paquete según tu proyecto

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository; // Necesario para buscar al vendedor por ID

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));
        return convertirAResponseDTO(producto);
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        // Buscar las entidades relacionadas mediante los IDs del DTO
        User vendedor = userRepository.findById(dto.getVendedorId())
                .orElseThrow(() -> new RuntimeException("Usuario/Vendedor no encontrado con ID: " + dto.getVendedorId()));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // Mapear el RequestDTO a la Entidad
        Producto producto = new Producto();
        producto.setTitulo(dto.getTitulo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setTipo(dto.getTipo());
        producto.setImagenUrl(dto.getImagenUrl());

        // Relaciones y valores por defecto
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria);
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setEstado(EstadoProducto.DISPONIBLE);

        Producto productoGuardado = productoRepository.save(producto);
        return convertirAResponseDTO(productoGuardado);
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));

        // Actualizar campos básicos
        productoExistente.setTitulo(dto.getTitulo());
        productoExistente.setDescripcion(dto.getDescripcion());
        productoExistente.setPrecio(dto.getPrecio());
        productoExistente.setTipo(dto.getTipo());

        if (dto.getEstado() != null) {
            productoExistente.setEstado(dto.getEstado());
        }
        if (dto.getImagenUrl() != null) {
            productoExistente.setImagenUrl(dto.getImagenUrl());
        }

        // Si se cambia de categoría en la edición
        if (dto.getCategoriaId() != null && !dto.getCategoriaId().equals(productoExistente.getCategoria().getId())) {
            Categoria nuevaCategoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            productoExistente.setCategoria(nuevaCategoria);
        }

        Producto productoActualizado = productoRepository.save(productoExistente);
        return convertirAResponseDTO(productoActualizado);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    // --- Filtrados adaptados a DTO ---

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorEstado(EstadoProducto estado) {
        return productoRepository.findByEstado(estado).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorTipo(TipoProducto tipo) {
        return productoRepository.findByTipo(tipo).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // --- MÉTODO AUXILIAR DE MAPEO (Entity -> ResponseDTO) ---
    private ProductoResponseDTO convertirAResponseDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setTipo(producto.getTipo());
        dto.setEstado(producto.getEstado());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setFechaPublicacion(producto.getFechaPublicacion());

        if (producto.getVendedor() != null) {
            dto.setVendedorId(producto.getVendedor().getId());
            dto.setVendedorNombre(producto.getVendedor().getNombre()); // Asegúrate de que User tenga getNombre()
        }

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return dto;
    }
}