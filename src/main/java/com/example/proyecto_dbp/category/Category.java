package com.example.proyecto_dbp.Category;

import com.example.proyecto_dbp.Product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @OneToMany (mappedBy = "category")
    private List<Product> productos = new ArrayList<>();
}
