package com.example.proyecto_dbp.Product;

import com.example.proyecto_dbp.Category.Category;
import com.example.proyecto_dbp.Category.CategoryRepository;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
import com.example.proyecto_dbp.exception.ForbiddenException;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.User;
import com.example.proyecto_dbp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productoRepository;
    private final CategoryRepository categoriaRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<ProductResponse> obtenerTodos(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse obtenerPorId(Long id) {
        Product producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + id));
        return toResponse(producto);
    }

    @Transactional
    public ProductResponse crearProduct(ProductRequest request) {
        User vendedor = securityUtils.getUsuarioAutenticado();

        Category categoria = categoriaRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: " + request.getCategoryId()));

        Product producto = new Product();
        producto.setTitulo(request.getTitulo());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setTipo(request.getTipo());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setVendedor(vendedor);
        producto.setCategory(categoria);
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setEstado(ProductStatus.DISPONIBLE);

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductResponse actualizarProduct(Long id, ProductRequest request) {
        Product producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el vendedor dueÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o puede editar
        if (!producto.getVendedor().getId().equals(usuarioAutenticado.getId())) {
            throw new ForbiddenException("No tienes permiso para editar este producto");
        }

        producto.setTitulo(request.getTitulo());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setTipo(request.getTipo());

        if (request.getImagenUrl() != null) {
            producto.setImagenUrl(request.getImagenUrl());
        }

        if (request.getCategoryId() != null) {
            Category categoria = categoriaRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada"));
            producto.setCategory(categoria);
        }

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProduct(Long id) {
        Product producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el dueÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o o un ADMIN puede eliminar
        if (!producto.getVendedor().getId().equals(usuarioAutenticado.getId())
                && !usuarioAutenticado.getRole().name().equals("ADMIN")) {
            throw new ForbiddenException("No tienes permiso para eliminar este producto");
        }

        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> filtrarPorCategory(Long categoriaId) {
        return productoRepository.findByCategoryId(categoriaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> filtrarPorEstado(ProductStatus estado) {
        return productoRepository.findByEstado(estado).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> filtrarPorTipo(ProductType tipo) {
        return productoRepository.findByTipo(tipo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse dto = modelMapper.map(p, ProductResponse.class);
        dto.setVendedorNombre(p.getVendedor().getNombre());
        dto.setVendedorEmail(p.getVendedor().getEmail());
        dto.setCategoryName(p.getCategory().getNombre());
        return dto;
    }
}