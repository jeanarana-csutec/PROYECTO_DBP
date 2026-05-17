package com.example.proyecto_dbp.Transaccion;

import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Resena.Resena;
import com.example.proyecto_dbp.User.User;
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
public class Transaccion {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;

    private Double monto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Muchas transacciones pertenecen a un comprador
    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private User comprador;

    // Muchas transacciones pertenecen a un vendedor
    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    // Muchas transacciones referencian un producto
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Una transacción genera máximo una reseña
    @OneToOne(mappedBy = "transaccion", cascade = CascadeType.ALL)
    private Resena resena;
}
