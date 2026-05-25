package com.example.proyecto_dbp.Transaction;

import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Review.Review;
import com.example.proyecto_dbp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table (name = "transacciones")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionType tipo;

    @Enumerated(EnumType.STRING)
    private TransactionStatus estado;

    private Double monto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Muchas transacciones pertenecen a un comprador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprador_id", nullable = false)
    private User comprador;

    // Muchas transacciones pertenecen a un vendedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    // Many transactions reference a product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;

    // A transaction can have at most one review
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Review review;
}
