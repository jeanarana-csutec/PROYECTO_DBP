package com.example.proyecto_dbp.Mensaje;

import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contenido;

    private LocalDateTime fechaEnvio;
    private Boolean leido = false;

    @PrePersist
    protected void onCreate() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
    }

    // Muchos mensajes tienen un emisor
    @ManyToOne
    @JoinColumn(name = "emisor_id", nullable = false)
    private User emisor;

    // Muchos mensajes tienen un receptor
    @ManyToOne
    @JoinColumn(name = "receptor_id", nullable = false)
    private User receptor;

    // Mensajes asociados a un producto específico
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
