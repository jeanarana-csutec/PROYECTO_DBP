// ResenaService.java
package com.example.proyecto_dbp.Resena;

import com.example.proyecto_dbp.Exceptions.*;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.Transaccion.EstadoTransaccion;
import com.example.proyecto_dbp.Transaccion.Transaccion;
import com.example.proyecto_dbp.Transaccion.TransaccionRepository;
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
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final TransaccionRepository transaccionRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;

    @Transactional
    public ResenaResponseDTO crear(ResenaRequestDTO request) {
        User autor = securityUtils.getUsuarioAutenticado();

        Transaccion transaccion = transaccionRepository.findById(request.getTransaccionId())
                .orElseThrow(() -> new ResourceNotFound("Transacción no encontrada con id: " + request.getTransaccionId()));

        // Solo el comprador puede reseñar
        if (!transaccion.getComprador().getId().equals(autor.getId())) {
            throw new Forbidden("Solo el comprador puede dejar una reseña");
        }

        // Solo si la transacción está completada
        if (!transaccion.getEstado().equals(EstadoTransaccion.COMPLETADA)) {
            throw new InvalidOperation("Solo puedes reseñar transacciones completadas");
        }

        // Solo una reseña por transacción
        if (resenaRepository.existsByTransaccionId(request.getTransaccionId())) {
            throw new InvalidOperation("Ya existe una reseña para esta transacción");
        }

        Resena resena = new Resena();
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario());
        resena.setTransaccion(transaccion);
        resena.setAutor(autor);
        resena.setFecha(LocalDateTime.now());

        return toResponse(resenaRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public ResenaResponseDTO obtenerPorId(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Reseña no encontrada con id: " + id));
        return toResponse(resena);
    }

    @Transactional(readOnly = true)
    public List<ResenaResponseDTO> obtenerPorAutor(Long autorId) {
        return resenaRepository.findByAutorId(autorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResenaResponseDTO obtenerPorTransaccion(Long transaccionId) {
        Resena resena = resenaRepository.findByTransaccionId(transaccionId)
                .orElseThrow(() -> new ResourceNotFound("No hay reseña para esta transacción"));
        return toResponse(resena);
    }

    @Transactional
    public void eliminar(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Reseña no encontrada con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el autor o un admin pueden eliminar
        if (!resena.getAutor().getId().equals(usuarioAutenticado.getId())
                && !usuarioAutenticado.getRol().name().equals("ADMIN")) {
            throw new Forbidden("No tienes permiso para eliminar esta reseña");
        }

        resenaRepository.deleteById(id);
    }

    private ResenaResponseDTO toResponse(Resena r) {
        ResenaResponseDTO dto = modelMapper.map(r, ResenaResponseDTO.class);
        dto.setAutorNombre(r.getAutor().getNombre());
        dto.setProductoTitulo(r.getTransaccion().getProducto().getTitulo());
        return dto;
    }
}