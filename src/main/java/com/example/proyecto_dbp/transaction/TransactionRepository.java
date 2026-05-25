// TransactionRepository.java
package com.example.proyecto_dbp.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompradorId(Long compradorId);
    Page<Transaction> findByCompradorId(Long compradorId, Pageable pageable);
    List<Transaction> findByVendedorId(Long vendedorId);
    Page<Transaction> findByVendedorId(Long vendedorId, Pageable pageable);
    List<Transaction> findByProductId(Long productoId);
    List<Transaction> findByEstado(TransactionStatus estado);
}