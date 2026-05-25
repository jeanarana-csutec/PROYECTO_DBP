// MessageService.java
package com.example.proyecto_dbp.Message;

import com.example.proyecto_dbp.event.NewMessageEvent;
import com.example.proyecto_dbp.exception.*;
import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Product.ProductRepository;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.User;
import com.example.proyecto_dbp.user.UserRepository;
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
public class MessageService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageRepository mensajeRepository;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final ProductRepository productoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public MessageResponse enviar(MessageRequest request) {
        User emisor = securityUtils.getUsuarioAutenticado();

        User receptor = userRepository.findById(request.getReceptorId())
                .orElseThrow(() -> new ResourceNotFoundException("Receptor no encontrado con id: " + request.getReceptorId()));

        // No puedes mandarte un mensaje a ti mismo
        if (emisor.getId().equals(receptor.getId())) {
            throw new MessageNotAllowedException("No puedes enviarte un mensaje a ti mismo");
        }

        Product producto = productoRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product no encontrado con id: " + request.getProductId()));

        Message mensaje = new Message();
        mensaje.setContenido(request.getContenido());
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setProduct(producto);
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);

        Message mensajeGuardado = mensajeRepository.save(mensaje);

        applicationEventPublisher.publishEvent(new NewMessageEvent(this, mensajeGuardado));

        return toResponse(mensajeGuardado);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> conversacion(Long productoId) {
        User usuario = securityUtils.getUsuarioAutenticado();
        return mensajeRepository.findByProductId(productoId).stream()
                .filter(m -> m.getEmisor().getId().equals(usuario.getId())
                        || m.getReceptor().getId().equals(usuario.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse marcarLeido(Long id) {
        Message mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el receptor puede marcar como leÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­do
        if (!mensaje.getReceptor().getId().equals(usuarioAutenticado.getId())) {
            throw new ForbiddenException("No tienes permiso para marcar este mensaje como leÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­do");
        }

        mensaje.setLeido(true);
        return toResponse(mensajeRepository.save(mensaje));
    }

    @Transactional
    public void eliminar(Long id) {
        Message mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message no encontrado con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el emisor puede eliminar su mensaje
        if (!mensaje.getEmisor().getId().equals(usuarioAutenticado.getId())) {
            throw new ForbiddenException("No tienes permiso para eliminar este mensaje");
        }

        mensajeRepository.deleteById(id);
    }

    private MessageResponse toResponse(Message m) {
        MessageResponse dto = modelMapper.map(m, MessageResponse.class);
        dto.setEmisorNombre(m.getEmisor().getNombre());
        dto.setReceptorNombre(m.getReceptor().getNombre());
        dto.setProductTitle(m.getProduct().getTitulo());
        return dto;
    }
}