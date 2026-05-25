package com.example.proyecto_dbp.Resena;

import com.example.proyecto_dbp.Transaccion.Transaccion;
import com.example.proyecto_dbp.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;

@Entity
@Table(name = "resenas")
@Getter
@Setter
@NoArgsConstructor
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1) @Max(5)
    private Integer puntuacion;

    private String comentario;
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    // Una reseña pertenece a una sola transacción (y solo puede haber una)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false, unique = true)
    private Transaccion transaccion;

    // Muchas reseñas son escritas por un autor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private User autor;
}
