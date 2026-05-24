// TransaccionService.java
package com.example.proyecto_dbp.Transaccion;

import com.example.proyecto_dbp.Events.TransaccionCompletadaEvent;
import com.example.proyecto_dbp.Exceptions.*;
import com.example.proyecto_dbp.Producto.EstadoProducto;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.ProductoRepository;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransaccionRepository transaccionRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Usuario no encontrado"));
    }

    @Transactional
    public TransaccionResponseDTO crear(TransaccionRequestDTO request) {
        User comprador = getUsuarioAutenticado();

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + request.getProductoId()));

        // No puedes comprar tu propio producto
        if (producto.getVendedor().getId().equals(comprador.getId())) {
            throw new InvalidOperation("No puedes comprar tu propio producto");
        }

        // Producto debe estar disponible
        if (!producto.getEstado().equals(EstadoProducto.DISPONIBLE)) {
            throw new ProductoNoDisponible("El producto no está disponible");
        }

        // Para alquiler necesita fechaFin
        if (request.getTipo().equals(TipoTransaccion.ALQUILER) && request.getFechaFin() == null) {
            throw new InvalidOperation("El alquiler requiere una fecha de fin");
        }

        Transaccion transaccion = new Transaccion();
        transaccion.setComprador(comprador);
        transaccion.setVendedor(producto.getVendedor());
        transaccion.setProducto(producto);
        transaccion.setTipo(request.getTipo());
        transaccion.setMonto(producto.getPrecio());
        transaccion.setFechaInicio(LocalDateTime.now());
        transaccion.setFechaFin(request.getFechaFin());
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);

        // Cambiar estado del producto
        if (request.getTipo().equals(TipoTransaccion.COMPRA)) {
            producto.setEstado(EstadoProducto.RESERVADO);
        } else {
            producto.setEstado(EstadoProducto.ALQUILADO);
        }
        productoRepository.save(producto);

        return toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional
    public TransaccionResponseDTO completar(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Transacción no encontrada con id: " + id));

        User usuarioAutenticado = getUsuarioAutenticado();

        // Solo el vendedor puede completar
        if (!transaccion.getVendedor().getId().equals(usuarioAutenticado.getId())) {
            throw new Forbidden("Solo el vendedor puede completar la transacción");
        }

        if (!transaccion.getEstado().equals(EstadoTransaccion.PENDIENTE)) {
            throw new InvalidOperation("Solo se pueden completar transacciones pendientes");
        }

        transaccion.setEstado(EstadoTransaccion.COMPLETADA);

        // Marcar producto como vendido o alquilado definitivamente
        Producto producto = transaccion.getProducto();
        if (transaccion.getTipo().equals(TipoTransaccion.COMPRA)) {
            producto.setEstado(EstadoProducto.VENDIDO);
        }
        productoRepository.save(producto);
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        applicationEventPublisher.publishEvent(new TransaccionCompletadaEvent(this, transaccionGuardada));
        return toResponse(transaccionGuardada);
    }

    @Transactional
    public TransaccionResponseDTO cancelar(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Transacción no encontrada con id: " + id));

        User usuarioAutenticado = getUsuarioAutenticado();

        // Solo comprador o vendedor pueden cancelar
        boolean esComprador = transaccion.getComprador().getId().equals(usuarioAutenticado.getId());
        boolean esVendedor = transaccion.getVendedor().getId().equals(usuarioAutenticado.getId());

        if (!esComprador && !esVendedor) {
            throw new Forbidden("No tienes permiso para cancelar esta transacción");
        }

        if (!transaccion.getEstado().equals(EstadoTransaccion.PENDIENTE)) {
            throw new InvalidOperation("Solo se pueden cancelar transacciones pendientes");
        }

        transaccion.setEstado(EstadoTransaccion.CANCELADA);

        // Devolver producto a disponible
        Producto producto = transaccion.getProducto();
        producto.setEstado(EstadoProducto.DISPONIBLE);
        productoRepository.save(producto);

        return toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> misCompras() {
        User usuario = getUsuarioAutenticado();
        return transaccionRepository.findByCompradorId(usuario.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> misVentas() {
        User usuario = getUsuarioAutenticado();
        return transaccionRepository.findByVendedorId(usuario.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransaccionResponseDTO obtenerPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Transacción no encontrada con id: " + id));
        return toResponse(transaccion);
    }

    private TransaccionResponseDTO toResponse(Transaccion t) {
        TransaccionResponseDTO dto = modelMapper.map(t, TransaccionResponseDTO.class);
        dto.setCompradorNombre(t.getComprador().getNombre());
        dto.setVendedorNombre(t.getVendedor().getNombre());
        dto.setProductoTitulo(t.getProducto().getTitulo());
        return dto;
    }
}