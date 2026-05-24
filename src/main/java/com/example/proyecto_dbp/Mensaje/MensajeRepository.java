// MensajeRepository.java
package com.example.proyecto_dbp.Mensaje;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByEmisorIdAndProductoId(Long emisorId, Long productoId);
    List<Mensaje> findByReceptorIdAndProductoId(Long receptorId, Long productoId);
    List<Mensaje> findByProductoId(Long productoId);
}