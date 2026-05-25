// ReviewService.java
package com.example.proyecto_dbp.Review;

import com.example.proyecto_dbp.exception.*;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.Transaction.TransactionStatus;
import com.example.proyecto_dbp.Transaction.Transaction;
import com.example.proyecto_dbp.Transaction.TransactionRepository;
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
public class ReviewService {

    private final ReviewRepository resenaRepository;
    private final TransactionRepository transaccionRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;

    @Transactional
    public ReviewResponse crear(ReviewRequest request) {
        User autor = securityUtils.getUsuarioAutenticado();

        Transaction transaccion = transaccionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n no encontrada con id: " + request.getTransactionId()));

        // Solo el comprador puede reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±ar
        if (!transaccion.getComprador().getId().equals(autor.getId())) {
            throw new ForbiddenException("Solo el comprador puede dejar una reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a");
        }

        // Solo si la transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ completada
        if (!transaccion.getEstado().equals(TransactionStatus.COMPLETADA)) {
            throw new InvalidOperationException("Solo puedes reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±ar transacciones completadas");
        }

        // Solo una reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a por transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
        if (resenaRepository.existsByTransactionId(request.getTransactionId())) {
            throw new InvalidOperationException("Ya existe una reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a para esta transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        }

        Review resena = new Review();
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario());
        resena.setTransaction(transaccion);
        resena.setAutor(autor);
        resena.setFecha(LocalDateTime.now());

        return toResponse(resenaRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public ReviewResponse obtenerPorId(Long id) {
        Review resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a no encontrada con id: " + id));
        return toResponse(resena);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> obtenerPorAutor(Long autorId) {
        return resenaRepository.findByAutorId(autorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse obtenerPorTransaction(Long transaccionId) {
        Review resena = resenaRepository.findByTransactionId(transaccionId)
                .orElseThrow(() -> new ResourceNotFoundException("No hay reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a para esta transacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n"));
        return toResponse(resena);
    }

    @Transactional
    public void eliminar(Long id) {
        Review resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a no encontrada con id: " + id));

        User usuarioAutenticado = securityUtils.getUsuarioAutenticado();

        // Solo el autor o un admin pueden eliminar
        if (!resena.getAutor().getId().equals(usuarioAutenticado.getId())
                && !usuarioAutenticado.getRole().name().equals("ADMIN")) {
            throw new ForbiddenException("No tienes permiso para eliminar esta reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a");
        }

        resenaRepository.deleteById(id);
    }

    private ReviewResponse toResponse(Review r) {
        ReviewResponse dto = modelMapper.map(r, ReviewResponse.class);
        dto.setAutorNombre(r.getAutor().getNombre());
        dto.setProductTitle(r.getTransaction().getProduct().getTitulo());
        return dto;
    }
}