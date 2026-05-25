// MensajeService.java
package com.example.proyecto_dbp.Mensaje;

import com.example.proyecto_dbp.Events.NuevoMensajeEvent;
import com.example.proyecto_dbp.Exceptions.*;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.ProductoRepository;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MensajeRepository mensajeRepository;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public MensajeResponseDTO enviar(MensajeRequestDTO request) {
        User emisor = securityUtils.getUsuarioAutenticado();

        User receptor = userRepository.findById(request.getReceptorId())
                .orElseThrow(() -> new ResourceNotFound("Receptor no encontrado con id: " + request.getReceptorId()));

        // No puedes mandarte un mensaje a ti mismo
        if (emisor.getId().equals(receptor.getId())) {
            throw new MensajeNoPermitido("No puedes enviarte un mensaje a ti mismo");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFound("Producto no encontrado con id: " + request.getProductoId()));

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(request.getContenido());
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setProducto(producto);
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);

        Mensaje mensajeGuardado = mensajeRepository.save(mensaje);

        applicationEventPublisher.publishEvent(new NuevoMensajeEvent(this, mensajeGuardado));

        return toResponse(mensajeGuardado);
    }

    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> conversacion(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();
        return mensajeRepository.findByProductoId(productoId).stream()
                .filter(m -> m.getEmisor().getId().equals(usuario.getId())
                        || m.getReceptor().getId().equals(usuario.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MensajeResponseDTO marcarLeido(Long id) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Mensaje no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el receptor puede marcar como leído
        if (!mensaje.getReceptor().getId().equals(usuarioAutenticado.getId())) {
            throw new Forbidden("No tienes permiso para marcar este mensaje como leído");
        }

        mensaje.setLeido(true);
        return toResponse(mensajeRepository.save(mensaje));
    }

    @Transactional
    public void eliminar(Long id) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Mensaje no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el emisor puede eliminar su mensaje
        if (!mensaje.getEmisor().getId().equals(usuarioAutenticado.getId())) {
            throw new Forbidden("No tienes permiso para eliminar este mensaje");
        }

        mensajeRepository.deleteById(id);
    }

    private MensajeResponseDTO toResponse(Mensaje m) {
        MensajeResponseDTO dto = modelMapper.map(m, MensajeResponseDTO.class);
        dto.setEmisorNombre(m.getEmisor().getNombre());
        dto.setReceptorNombre(m.getReceptor().getNombre());
        dto.setProductoTitulo(m.getProducto().getTitulo());
        return dto;
    }
}