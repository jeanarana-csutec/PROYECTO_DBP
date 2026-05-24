// CategoriaRepository.java
package com.example.proyecto_dbp.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}