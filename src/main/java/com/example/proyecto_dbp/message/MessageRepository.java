// MessageRepository.java
package com.example.proyecto_dbp.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEmisorIdAndProductId(Long emisorId, Long productoId);
    List<Message> findByReceptorIdAndProductId(Long receptorId, Long productoId);
    List<Message> findByProductId(Long productoId);
}