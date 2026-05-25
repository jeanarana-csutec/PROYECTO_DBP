package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Categoria.CategoriaRepository;
import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import com.example.proyecto_dbp.Exceptions.Forbidden;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ModelMapper modelMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + id));
        return toResponse(producto);
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        User vendedor = securityUtils.getUsuarioAutenticado();

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFound("Categoría no encontrada con id: " + request.getCategoriaId()));

        Producto producto = new Producto();
        producto.setTitulo(request.getTitulo());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setTipo(request.getTipo());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria);
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setEstado(EstadoProducto.DISPONIBLE);

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el vendedor dueño puede editar
        if (!producto.getVendedor().getId().equals(usuarioAutenticado.getId())) {
            throw new Forbidden("No tienes permiso para editar este producto");
        }

        producto.setTitulo(request.getTitulo());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setTipo(request.getTipo());

        if (request.getImagenUrl() != null) {
            producto.setImagenUrl(request.getImagenUrl());
        }

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFound("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el dueño o un ADMIN puede eliminar
        if (!producto.getVendedor().getId().equals(usuarioAutenticado.getId())
                && !usuarioAutenticado.getRol().name().equals("ADMIN")) {
            throw new Forbidden("No tienes permiso para eliminar este producto");
        }

        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorEstado(EstadoProducto estado) {
        return productoRepository.findByEstado(estado).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> filtrarPorTipo(TipoProducto tipo) {
        return productoRepository.findByTipo(tipo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProductoResponseDTO toResponse(Producto p) {
        ProductoResponseDTO dto = modelMapper.map(p, ProductoResponseDTO.class);
        dto.setVendedorNombre(p.getVendedor().getNombre());
        dto.setVendedorEmail(p.getVendedor().getEmail());
        dto.setCategoriaNombre(p.getCategoria().getNombre());
        return dto;
    }
}