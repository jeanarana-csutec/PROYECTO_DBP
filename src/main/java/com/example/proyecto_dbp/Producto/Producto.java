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

    // Muchos productos pertenecen a un vendedor
    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    // Muchos productos pertenecen a una categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Un producto puede tener muchas transacciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<Transaccion> transacciones = new ArrayList<>();

    // Un producto puede tener muchos mensajes asociados
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new ArrayList<>();

    // Usuarios que marcaron este producto como favorito
    @ManyToMany(mappedBy = "favoritos")
    private List<User> usuariosQueFavoritaron = new ArrayList<>();
}
