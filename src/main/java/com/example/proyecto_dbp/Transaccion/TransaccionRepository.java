// TransaccionRepository.java
package com.example.proyecto_dbp.Transaccion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCompradorId(Long compradorId);
    Page<Transaccion> findByCompradorId(Long compradorId, Pageable pageable);
    List<Transaccion> findByVendedorId(Long vendedorId);
    Page<Transaccion> findByVendedorId(Long vendedorId, Pageable pageable);
    List<Transaccion> findByProductoId(Long productoId);
    List<Transaccion> findByEstado(EstadoTransaccion estado);
}