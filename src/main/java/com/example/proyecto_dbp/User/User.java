package com.example.proyecto_dbp.User;

import com.example.proyecto_dbp.Mensaje.Mensaje;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Transaccion.Transaccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column (nullable = false, unique = true)
    private String email;
    @Column (nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private String universidad;
    private String fotoUrl;
    private LocalDateTime fechaRegistro;
    private Boolean activo = true;

    // Un usuario publica muchos productos
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();
    // Un usuario hace muchas transacciones como comprador
    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL)
    private List<Transaccion> compras = new ArrayList<>();
    // Un usuario recibe muchas transacciones como vendedor
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Transaccion> ventas = new ArrayList<>();
    // Favoritos (ManyToMany)
    @ManyToMany
    @JoinTable(
            name = "favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> favoritos = new ArrayList<>();
    @OneToMany(mappedBy = "emisor")
    private List<Mensaje> mensajesEnviados = new ArrayList<>();
    @OneToMany(mappedBy = "receptor")
    private List<Mensaje> mensajesRecibidos = new ArrayList<>();
}
