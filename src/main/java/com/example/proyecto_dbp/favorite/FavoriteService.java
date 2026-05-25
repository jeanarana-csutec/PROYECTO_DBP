// FavoriteService.java
package com.example.proyecto_dbp.Favorite;

import com.example.proyecto_dbp.exception.*;
import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Product.ProductRepository;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoritoRepository;
    private final ProductRepository productoRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;

    @Transactional
    public FavoriteResponse agregar(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();

        Product producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + productoId));

        // No puedes marcar tu propio producto como favorito
        if (producto.getVendedor().getId().equals(usuario.getId())) {
            throw new InvalidOperationException("No puedes marcar tu propio producto como favorito");
        }

        // Ya estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ en favoritos
        if (favoritoRepository.existsByUserIdAndProductId(usuario.getId(), productoId)) {
            throw new InvalidOperationException("Este producto ya estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ en tus favoritos");
        }

        Favorite favorito = new Favorite();
        favorito.setUser(usuario);
        favorito.setProduct(producto);
        favorito.setFechaAgregado(LocalDateTime.now());

        return toResponse(favoritoRepository.save(favorito));
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> misFavorites() {
        User usuario = securityUtils.getUsuarioAutenticado();
        return favoritoRepository.findByUserId(usuario.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();

        Favorite favorito = favoritoRepository
                .findByUserIdAndProductId(usuario.getId(), productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Este producto no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ en tus favoritos"));

        favoritoRepository.deleteById(favorito.getId());
    }

    private FavoriteResponse toResponse(Favorite f) {
        FavoriteResponse dto = modelMapper.map(f, FavoriteResponse.class);
        dto.setProductId(f.getProduct().getId());
        dto.setProductTitle(f.getProduct().getTitulo());
        dto.setProductPrice(f.getProduct().getPrecio());
        dto.setFechaAgregado(f.getFechaAgregado());
        return dto;
    }
}