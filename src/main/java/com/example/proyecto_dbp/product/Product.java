package com.example.proyecto_dbp.Product;

import com.example.proyecto_dbp.Category.Category;
import com.example.proyecto_dbp.Message.Message;
import com.example.proyecto_dbp.Transaction.Transaction;
import com.example.proyecto_dbp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.PrePersist;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    private ProductType tipo;

    @Enumerated(EnumType.STRING)
    private ProductStatus estado;

    private String imagenUrl;
    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
    }

    // Muchos productos pertenecen a un vendedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    // Many products belong to a category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category category;

    // A product can have many transactions
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    // A product can have many associated messages
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

}
