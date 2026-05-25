package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Categoria.CategoriaRepository;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UserRepository userRepository;

    // ===== MÉTODO AUXILIAR =====
    private Producto crearProductoBase() {

        User user = new User();
        user.setNombre("Itta");
        user.setEmail("itta@utp.edu.pe");
        user.setPassword("123456");
        user.setRol(Rol.USER);

        User savedUser = userRepository.save(user);

        Categoria categoria = new Categoria();
        categoria.setNombre("Tecnología");

        Categoria savedCategoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setTitulo("Laptop Gamer");
        producto.setDescripcion("RTX 4060");
        producto.setPrecio(3500.0);
        producto.setTipo(TipoProducto.VENTA);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setCategoria(savedCategoria);
        producto.setVendedor(savedUser);

        return producto;
    }

    @Test
    void shouldSaveProductSuccessfully() {

        Producto producto = crearProductoBase();

        Producto savedProducto = productoRepository.save(producto);

        assertNotNull(savedProducto.getId());
        assertEquals("Laptop Gamer", savedProducto.getTitulo());
        assertEquals(3500.0, savedProducto.getPrecio());
    }

    @Test
    void shouldFindProductsByCategoriaIdWhenCategoriaExists() {

        Producto producto = crearProductoBase();

        Producto savedProducto = productoRepository.save(producto);

        List<Producto> productos =
                productoRepository.findByCategoriaId(
                        savedProducto.getCategoria().getId()
                );

        assertFalse(productos.isEmpty());
        assertEquals(1, productos.size());
    }

    @Test
    void shouldReturnEmptyListWhenCategoriaDoesNotExist() {

        List<Producto> productos =
                productoRepository.findByCategoriaId(999L);

        assertTrue(productos.isEmpty());
    }

    @Test
    void shouldFindProductsByTipoWhenTipoExists() {

        Producto producto = crearProductoBase();

        productoRepository.save(producto);

        List<Producto> productos =
                productoRepository.findByTipo(TipoProducto.VENTA);

        assertFalse(productos.isEmpty());
        assertEquals(TipoProducto.VENTA, productos.getFirst().getTipo());
    }

    @Test
    void shouldFindProductsByEstadoWhenEstadoExists() {

        Producto producto = crearProductoBase();

        productoRepository.save(producto);

        List<Producto> productos =
                productoRepository.findByEstado(EstadoProducto.DISPONIBLE);

        assertFalse(productos.isEmpty());
        assertEquals(
                EstadoProducto.DISPONIBLE,
                productos.getFirst().getEstado()
        );
    }

    @Test
    void shouldFindProductsByVendedorIdWhenVendedorExists() {

        Producto producto = crearProductoBase();

        Producto savedProducto = productoRepository.save(producto);

        Long vendedorId =
                savedProducto.getVendedor().getId();

        List<Producto> productos =
                productoRepository.findByVendedorId(vendedorId);

        assertFalse(productos.isEmpty());
        assertEquals(1, productos.size());
    }
}