// TransaccionRepository.java
package com.example.proyecto_dbp.Transaccion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCompradorId(Long compradorId);
    List<Transaccion> findByVendedorId(Long vendedorId);
    List<Transaccion> findByProductoId(Long productoId);
    List<Transaccion> findByEstado(EstadoTransaccion estado);
}