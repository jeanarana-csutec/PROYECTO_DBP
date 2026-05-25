package com.example.proyecto_dbp.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Sistema de bÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºsqueda y filtrado sugerido en el documento
    List<Product> findByCategoryId(Long categoriaId);

    List<Product> findByTipo(ProductType tipo);

    List<Product> findByEstado(ProductStatus estado);

    List<Product> findByVendedorId(Long vendedorId);
}