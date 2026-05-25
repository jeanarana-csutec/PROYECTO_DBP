// ReviewRepository.java
package com.example.proyecto_dbp.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByTransactionId(Long transaccionId);
    List<Review> findByAutorId(Long autorId);
    boolean existsByTransactionId(Long transaccionId);
}