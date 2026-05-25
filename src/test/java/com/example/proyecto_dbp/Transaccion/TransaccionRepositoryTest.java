package com.example.proyecto_dbp.Transaccion;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Producto.EstadoProducto;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.TipoProducto;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
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
@DisplayName("Transaccion Repository Tests")
class TransaccionRepositoryTest {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User comprador;
    private User vendedor;
    private Categoria categoria;
    private Producto producto;
    private Transaccion transaccion;

    @BeforeEach
    void setUp() {
        comprador = new User();
        comprador.setNombre("Juan Perez");
        comprador.setEmail("juan@test.com");
        comprador.setPassword("password");
        comprador.setRol(Rol.USER);
        comprador.setActivo(true);
        comprador.setFechaRegistro(LocalDateTime.now());
        entityManager.persist(comprador);

        vendedor = new User();
        vendedor.setNombre("Maria Lopez");
        vendedor.setEmail("maria@test.com");
        vendedor.setPassword("password");
        vendedor.setRol(Rol.USER);
        vendedor.setActivo(true);
        vendedor.setFechaRegistro(LocalDateTime.now());
        entityManager.persist(vendedor);

        // 🔧 CREAR CATEGORÍA PRIMERO
        categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");
        entityManager.persist(categoria);

        producto = new Producto();
        producto.setTitulo("iPhone 14");
        producto.setDescripcion("Teléfono Apple");
        producto.setPrecio(1000.0);
        producto.setTipo(TipoProducto.VENTA);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria);  // 🔧 ASIGNAR CATEGORÍA
        producto.setFechaPublicacion(LocalDateTime.now());
        entityManager.persist(producto);

        transaccion = new Transaccion();
        transaccion.setComprador(comprador);
        transaccion.setVendedor(vendedor);
        transaccion.setProducto(producto);
        transaccion.setTipo(TipoTransaccion.COMPRA);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
        transaccion.setMonto(1000.0);
        transaccion.setFechaInicio(LocalDateTime.now());
    }

    @Test
    @DisplayName("should save transaccion when valid data")
    void shouldSaveTransaccionWhenValidData() {
        // When
        Transaccion saved = transaccionRepository.save(transaccion);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEstado()).isEqualTo(EstadoTransaccion.PENDIENTE);
        assertThat(saved.getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transaccion by id when exists")
    void shouldFindByIdWhenExists() {
        // Given
        Transaccion saved = entityManager.persistAndFlush(transaccion);

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

        Transaccion transaccion2 = new Transaccion();
        transaccion2.setComprador(comprador);
        transaccion2.setVendedor(vendedor);
        transaccion2.setProducto(producto);
        transaccion2.setTipo(TipoTransaccion.COMPRA);
        transaccion2.setEstado(EstadoTransaccion.PENDIENTE);
        transaccion2.setMonto(500.0);
        transaccion2.setFechaInicio(LocalDateTime.now());
        entityManager.persistAndFlush(transaccion2);

        // When
        List<Transaccion> compras = transaccionRepository.findByCompradorId(comprador.getId());

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
        List<Transaccion> ventas = transaccionRepository.findByVendedorId(vendedor.getId());

        // Then
        assertThat(ventas).hasSize(1);
        assertThat(ventas.get(0).getMonto()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("should find transacciones by producto id")
    void shouldFindByProductoId() {
        // Given
        entityManager.persistAndFlush(transaccion);

        // When
        List<Transaccion> transacciones = transaccionRepository.findByProductoId(producto.getId());

        // Then
        assertThat(transacciones).hasSize(1);
    }

    @Test
    @DisplayName("should find transacciones by estado")
    void shouldFindByEstado() {
        // Given
        entityManager.persistAndFlush(transaccion);

        // When
        List<Transaccion> pendientes = transaccionRepository.findByEstado(EstadoTransaccion.PENDIENTE);

        // Then
        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).getEstado()).isEqualTo(EstadoTransaccion.PENDIENTE);
    }

    @Test
    @DisplayName("should update transaccion estado")
    void shouldUpdateTransaccionEstado() {
        // Given
        Transaccion saved = entityManager.persistAndFlush(transaccion);

        // When
        saved.setEstado(EstadoTransaccion.COMPLETADA);
        Transaccion updated = transaccionRepository.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getEstado()).isEqualTo(EstadoTransaccion.COMPLETADA);
    }

    @Test
    @DisplayName("should return empty list when no transacciones for comprador")
    void shouldReturnEmptyListWhenNoTransaccionesForComprador() {
        // When
        List<Transaccion> transacciones = transaccionRepository.findByCompradorId(999L);

        // Then
        assertThat(transacciones).isEmpty();
    }
}