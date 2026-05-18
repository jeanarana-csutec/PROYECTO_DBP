package com.example.proyecto_dbp.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Útil para validar que no se creen categorías con el mismo nombre
    Optional<Categoria> findByNombre(String nombre);
}