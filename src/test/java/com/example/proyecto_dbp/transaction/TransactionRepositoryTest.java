package com.example.proyecto_dbp.Transaction;

import com.example.proyecto_dbp.Category.Category;
import com.example.proyecto_dbp.Product.ProductStatus;
import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Product.ProductType;
import com.example.proyecto_dbp.user.Role;
import com.example.proyecto_dbp.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Transaction Repository Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transaccionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User comprador;
    private User vendedor;
    private Category categoria;
    private Product producto;
    private Transaction transaccion;

    @BeforeEach
    void setUp() {
        comprador = new User();
        comprador.setNombre("Juan Perez");
        comprador.setEmail("juan@test.com");
        comprador.setPassword("password");
        comprador.setRole(Role.USER);
        comprador.setActivo(true);
        comprador.setFechaRegistro(LocalDateTime.now());
        entityManager.persist(comprador);

        vendedor = new User();
        vendedor.setNombre("Maria Lopez");
        vendedor.setEmail("maria@test.com");
        vendedor.setPassword("password");
        vendedor.setRole(Role.USER);
        vendedor.setActivo(true);
        vendedor.setFechaRegistro(LocalDateTime.now());
        entityManager.persist(vendedor);

        // ÃƒÆ’Ã‚Â°Ãƒâ€¦Ã‚Â¸ÃƒÂ¢Ã¢â€šÂ¬Ã‚ÂÃƒâ€šÃ‚Â§ CREAR CATEGORÃƒÆ’Ã†â€™Ãƒâ€šÃ‚ÂA PRIMERO
        categoria = new Category();
        categoria.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        categoria.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");
        entityManager.persist(categoria);

        producto = new Product();
        producto.setTitulo("iPhone 14");
        producto.setDescripcion("TelÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©fono Apple");
        producto.setPrecio(1000.0);
        producto.setTipo(ProductType.VENTA);
        producto.setEstado(ProductStatus.DISPONIBLE);
        producto.setVendedor(vendedor);
        producto.setCategory(categoria);  // ÃƒÆ’Ã‚Â°Ãƒâ€¦Ã‚Â¸ÃƒÂ¢Ã¢â€šÂ¬Ã‚ÂÃƒâ€šÃ‚Â§ ASIGNAR CATEGORÃƒÆ’Ã†â€™Ãƒâ€šÃ‚ÂA
        producto.setFechaPublicacion(LocalDateTime.now());
        entityManager.persist(producto);

        transaccion = new Transaction();
        transaccion.setComprador(comprador);
        transaccion.setVendedor(vendedor);
        transaccion.setProduct(producto);
        transaccion.setTipo(TransactionType.COMPRA);
        transaccion.setEstado(TransactionStatus.PENDIENTE);
        transaccion.setMonto(1000.0);
        transaccion.setFechaInicio(LocalDateTime.now());
    }

    @Test
    @DisplayName("should save transaccion when valid data")
    void shouldSaveTransactionWhenValidData() {
        // When
        Transaction saved = transaccionRepository.save(transaccion);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEstado()).isEqualTo(TransactionStatus.PENDIENTE);
        assertThat(saved.getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transaccion by id when exists")
    void shouldFindByIdWhenExists() {
        // Given
        Transaction saved = entityManager.persistAndFlush(transaccion);

        // When
        var found = transaccionRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transacciones by comprador id")
    void shouldFindByCompradorId() {
        // Given
        entityManager.persistAndFlush(transaccion);

        Transaction transaccion2 = new Transaction();
        transaccion2.setComprador(comprador);
        transaccion2.setVendedor(vendedor);
        transaccion2.setProduct(producto);
        transaccion2.setTipo(TransactionType.COMPRA);
        transaccion2.setEstado(TransactionStatus.PENDIENTE);
        transaccion2.setMonto(500.0);
        transaccion2.setFechaInicio(LocalDateTime.now());
        entityManager.persistAndFlush(transaccion2);

        // When
        List<Transaction> compras = transaccionRepository.findByCompradorId(comprador.getId());

        // Then
        assertThat(compras).hasSize(2);
        assertThat(compras.get(0).getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transacciones by vendedor id")
    void shouldFindByVendedorId() {
        // Given
        entityManager.persistAndFlush(transaccion);

        // When
        List<Transaction> ventas = transaccionRepository.findByVendedorId(vendedor.getId());

        // Then
        assertThat(ventas).hasSize(1);
        assertThat(ventas.get(0).getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transacciones by producto id")
    void shouldFindByProductId() {
        // Given
        entityManager.persistAndFlush(transaccion);

        // When
        List<Transaction> transacciones = transaccionRepository.findByProductId(producto.getId());

        // Then
        assertThat(transacciones).hasSize(1);
    }

    @Test
    @DisplayName("should find transacciones by estado")
    void shouldFindByEstado() {
        // Given
        entityManager.persistAndFlush(transaccion);

        // When
        List<Transaction> pendientes = transaccionRepository.findByEstado(TransactionStatus.PENDIENTE);

        // Then
        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).getEstado()).isEqualTo(TransactionStatus.PENDIENTE);
    }

    @Test
    @DisplayName("should update transaccion estado")
    void shouldUpdateTransactionEstado() {
        // Given
        Transaction saved = entityManager.persistAndFlush(transaccion);

        // When
        saved.setEstado(TransactionStatus.COMPLETADA);
        Transaction updated = transaccionRepository.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getEstado()).isEqualTo(TransactionStatus.COMPLETADA);
    }

    @Test
    @DisplayName("should return empty list when no transacciones for comprador")
    void shouldReturnEmptyListWhenNoTransactionesForComprador() {
        // When
        List<Transaction> transacciones = transaccionRepository.findByCompradorId(999L);

        // Then
        assertThat(transacciones).isEmpty();
    }
}