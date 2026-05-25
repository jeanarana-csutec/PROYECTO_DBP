package com.example.proyecto_dbp.Review;

import com.example.proyecto_dbp.Transaction.Transaction;
import com.example.proyecto_dbp.user.User;
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
public class Review {
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

    // A review belongs to a single transaction (and there can only be one)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false, unique = true)
    private Transaction transaction;

    // Muchas reseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as son escritas por un autor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private User autor;
}
