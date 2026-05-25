// TransactionService.java
package com.example.proyecto_dbp.Transaction;

import com.example.proyecto_dbp.event.TransactionCompletedEvent;
import com.example.proyecto_dbp.exception.*;
import com.example.proyecto_dbp.Product.ProductStatus;
import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Product.ProductRepository;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionRepository transaccionRepository;
    private final ProductRepository productoRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;

    @Transactional
    public TransactionResponse crear(TransactionRequest request) {
        User comprador = securityUtils.getUsuarioAutenticado();

        Product producto = productoRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + request.getProductId()));

        // No puedes comprar tu propio producto
        if (producto.getVendedor().getId().equals(comprador.getId())) {
            throw new InvalidOperationException("No puedes comprar tu propio producto");
        }

        // Product debe estar disponible
        if (!producto.getEstado().equals(ProductStatus.DISPONIBLE)) {
            throw new ProductNotAvailableException("El producto no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ disponible");
        }

        // Para alquiler necesita fechaFin
        if (request.getTipo().equals(TransactionType.ALQUILER) && request.getFechaFin() == null) {
            throw new InvalidOperationException("El alquiler requiere una fecha de fin");
        }

        Transaction transaccion = new Transaction();
        transaccion.setComprador(comprador);
        transaccion.setVendedor(producto.getVendedor());
        transaccion.setProduct(producto);
        transaccion.setTipo(request.getTipo());
        transaccion.setMonto(producto.getPrecio());
        transaccion.setFechaInicio(LocalDateTime.now());
        transaccion.setFechaFin(request.getFechaFin());
        transaccion.setEstado(TransactionStatus.PENDIENTE);

        // Cambiar estado del producto
        if (request.getTipo().equals(TransactionType.COMPRA)) {
            producto.setEstado(ProductStatus.RESERVADO);
        } else {
            producto.setEstado(ProductStatus.ALQUILADO);
        }
        productoRepository.save(producto);

        return toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional
    public TransactionResponse completar(Long id) {
        Transaction transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n no encontrada con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el vendedor puede completar
        if (!transaccion.getVendedor().getId().equals(usuarioAutenticado.getId())) {
            throw new ForbiddenException("Solo el vendedor puede completar la transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        }

        if (!transaccion.getEstado().equals(TransactionStatus.PENDIENTE)) {
            throw new InvalidOperationException("Solo se pueden completar transacciones pendientes");
        }

        transaccion.setEstado(TransactionStatus.COMPLETADA);

        // Marcar producto como vendido o alquilado definitivamente
        Product producto = transaccion.getProduct();
        if (transaccion.getTipo().equals(TransactionType.COMPRA)) {
            producto.setEstado(ProductStatus.VENDIDO);
        }
        productoRepository.save(producto);
        Transaction transaccionGuardada = transaccionRepository.save(transaccion);
        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(this, transaccionGuardada));
        return toResponse(transaccionGuardada);
    }

    @Transactional
    public TransactionResponse cancelar(Long id) {
        Transaction transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n no encontrada con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo comprador o vendedor pueden cancelar
        boolean esComprador = transaccion.getComprador().getId().equals(usuarioAutenticado.getId());
        boolean esVendedor = transaccion.getVendedor().getId().equals(usuarioAutenticado.getId());

        if (!esComprador && !esVendedor) {
            throw new ForbiddenException("No tienes permiso para cancelar esta transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        }

        if (!transaccion.getEstado().equals(TransactionStatus.PENDIENTE)) {
            throw new InvalidOperationException("Solo se pueden cancelar transacciones pendientes");
        }

        transaccion.setEstado(TransactionStatus.CANCELADA);

        // Devolver producto a disponible
        Product producto = transaccion.getProduct();
        producto.setEstado(ProductStatus.DISPONIBLE);
        productoRepository.save(producto);

        return toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> misCompras(Pageable pageable) {
        User usuario = securityUtils.getUsuarioAutenticado();
        return transaccionRepository.findByCompradorId(usuario.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> misVentas(Pageable pageable) {
        User usuario = securityUtils.getUsuarioAutenticado();
        return transaccionRepository.findByVendedorId(usuario.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse obtenerPorId(Long id) {
        Transaction transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n no encontrada con id: " + id));
        return toResponse(transaccion);
    }

    private TransactionResponse toResponse(Transaction t) {
        TransactionResponse dto = modelMapper.map(t, TransactionResponse.class);
        dto.setCompradorNombre(t.getComprador().getNombre());
        dto.setVendedorNombre(t.getVendedor().getNombre());
        dto.setProductTitle(t.getProduct().getTitulo());
        return dto;
    }
}