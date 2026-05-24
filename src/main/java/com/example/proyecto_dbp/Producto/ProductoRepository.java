package com.example.proyecto_dbp.Producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Sistema de búsqueda y filtrado sugerido en el documento
    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByTipo(TipoProducto tipo);

    List<Producto> findByEstado(EstadoProducto estado);

    List<Producto> findByVendedorId(Long vendedorId);
}