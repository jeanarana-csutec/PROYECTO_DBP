package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Mensaje.Mensaje;
import com.example.proyecto_dbp.Transaccion.Transaccion;
import com.example.proyecto_dbp.User.User;
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
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipo;

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;

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

    // Muchos productos pertenecen a una categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Un producto puede tener muchas transacciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<Transaccion> transacciones = new ArrayList<>();

    // Un producto puede tener muchos mensajes asociados
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new ArrayList<>();

}
