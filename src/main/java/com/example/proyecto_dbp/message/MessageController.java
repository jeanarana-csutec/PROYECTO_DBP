// MessageController.java
package com.example.proyecto_dbp.Message;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService mensajeService;

    @PostMapping
    public ResponseEntity<MessageResponse> enviar(@Valid @RequestBody MessageRequest request) {
        return new ResponseEntity<>(mensajeService.enviar(request), HttpStatus.CREATED);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MessageResponse>> conversacion(@PathVariable Long productoId) {
        return ResponseEntity.ok(mensajeService.conversacion(productoId));
    }

    @PatchMapping("/{id}/leido")
    public ResponseEntity<MessageResponse> marcarLeido(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.marcarLeido(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRolee('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mensajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}