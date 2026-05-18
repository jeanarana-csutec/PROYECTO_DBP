package com.example.proyecto_dbp.Favorito;


import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.ProductoRepository;
import com.example.proyecto_dbp.Producto.ProductoResponseDTO;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;

    @Transactional
    public FavoritoResponseDTO agregarFavorito(FavoritoRequestDTO dto) {
        // 1. Validar duplicados de negocio
        if (favoritoRepository.existsByUsuarioIdAndProductoId(dto.getUsuarioId(), dto.getProductoId())) {
            throw new RuntimeException("El producto ya se encuentra en la lista de favoritos del usuario.");
        }

        // 2. Buscar entidades relacionadas
        User usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));

        // 3. Crear y persistir la entidad intermedia
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setProducto(producto);
        favorito.setFechaAgregado(LocalDateTime.now());

        Favorito favoritoGuardado = favoritoRepository.save(favorito);
        return convertirAResponseDTO(favoritoGuardado);
    }

    @Transactional
    public void eliminarFavorito(Long usuarioId, Long productoId) {
        Favorito favorito = favoritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElseThrow(() -> new RuntimeException("El producto no está en la lista de favoritos de este usuario."));

        favoritoRepository.delete(favorito);
    }

    @Transactional(readOnly = true)
    public List<FavoritoResponseDTO> obtenerFavoritosPorUsuario(Long usuarioId) {
        if (!userRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        return favoritoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }


    private FavoritoResponseDTO convertirAResponseDTO(Favorito favorito) {
        FavoritoResponseDTO dto = new FavoritoResponseDTO();
        dto.setId(favorito.getId());
        dto.setFechaAgregado(favorito.getFechaAgregado());
        dto.setProducto(mapearProductoADTO(favorito.getProducto()));
        return dto;
    }

    private ProductoResponseDTO mapearProductoADTO(Producto producto) {
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
            dto.setVendedorNombre(producto.getVendedor().getNombre());
        }

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return dto;
    }
}