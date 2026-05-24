// ResenaRepository.java
package com.example.proyecto_dbp.Resena;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    Optional<Resena> findByTransaccionId(Long transaccionId);
    List<Resena> findByAutorId(Long autorId);
    boolean existsByTransaccionId(Long transaccionId);
}