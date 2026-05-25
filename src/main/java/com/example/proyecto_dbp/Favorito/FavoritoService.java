// FavoritoService.java
package com.example.proyecto_dbp.Favorito;

import com.example.proyecto_dbp.Exceptions.*;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.ProductoRepository;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.User.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ProductoRepository productoRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;

    @Transactional
    public FavoritoResponseDTO agregar(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + productoId));

        // No puedes marcar tu propio producto como favorito
        if (producto.getVendedor().getId().equals(usuario.getId())) {
            throw new InvalidOperation("No puedes marcar tu propio producto como favorito");
        }

        // Ya está en favoritos
        if (favoritoRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId)) {
            throw new InvalidOperation("Este producto ya está en tus favoritos");
        }

        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setProducto(producto);
        favorito.setFechaAgregado(LocalDateTime.now());

        return toResponse(favoritoRepository.save(favorito));
    }

    @Transactional(readOnly = true)
    public List<FavoritoResponseDTO> misFavoritos() {
        User usuario = securityUtils.getUsuarioAutenticado();
        return favoritoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();

        Favorito favorito = favoritoRepository
                .findByUsuarioIdAndProductoId(usuario.getId(), productoId)
                .orElseThrow(() -> new ResourceNotFound("Este producto no está en tus favoritos"));

        favoritoRepository.deleteById(favorito.getId());
    }

    private FavoritoResponseDTO toResponse(Favorito f) {
        FavoritoResponseDTO dto = modelMapper.map(f, FavoritoResponseDTO.class);
        dto.setProductoId(f.getProducto().getId());
        dto.setProductoTitulo(f.getProducto().getTitulo());
        dto.setProductoPrecio(f.getProducto().getPrecio());
        dto.setFechaAgregado(f.getFechaAgregado());
        return dto;
    }
}