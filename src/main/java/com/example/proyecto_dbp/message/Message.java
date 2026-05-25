package com.example.proyecto_dbp.Message;

import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.user.User;
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
public class Message {
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private User emisor;

    // Muchos mensajes tienen un receptor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id", nullable = false)
    private User receptor;

    // Messages associated with a specific product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;
}
