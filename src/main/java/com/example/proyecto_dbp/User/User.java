package com.example.proyecto_dbp.User;

import com.example.proyecto_dbp.Mensaje.Mensaje;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Transaccion.Transaccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.PrePersist;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "users")
public class User implements UserDetails {
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
    @Column(nullable = false)
    private Rol rol = Rol.USER;
    private String universidad;
    private String fotoUrl;
    private LocalDateTime fechaRegistro;
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    // Un usuario publica muchos productos
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();
    // Un usuario hace muchas transacciones como comprador
    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL)
    private List<Transaccion> compras = new ArrayList<>();
    // Un usuario recibe muchas transacciones como vendedor
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Transaccion> ventas = new ArrayList<>();
    @OneToMany(mappedBy = "emisor")
    private List<Mensaje> mensajesEnviados = new ArrayList<>();
    @OneToMany(mappedBy = "receptor")
    private List<Mensaje> mensajesRecibidos = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+ rol.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return activo;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
