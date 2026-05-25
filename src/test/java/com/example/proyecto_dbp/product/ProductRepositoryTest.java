package com.example.proyecto_dbp.Product;

import com.example.proyecto_dbp.Category.Category;
import com.example.proyecto_dbp.Category.CategoryRepository;
import com.example.proyecto_dbp.user.Role;
import com.example.proyecto_dbp.user.User;
import com.example.proyecto_dbp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productoRepository;

    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private UserRepository userRepository;


    private Product crearProductBase() {

        User user = new User();
        user.setNombre("Itta");
        user.setEmail("itta@utp.edu.pe");
        user.setPassword("123456");
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        Category categoria = new Category();
        categoria.setNombre("TecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");

        Category savedCategory = categoriaRepository.save(categoria);

        Product producto = new Product();
        producto.setTitulo("Laptop Gamer");
        producto.setDescripcion("RTX 4060");
        producto.setPrecio(3500.0);
        producto.setTipo(ProductType.VENTA);
        producto.setEstado(ProductStatus.DISPONIBLE);
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setCategory(savedCategory);
        producto.setVendedor(savedUser);

        return producto;
    }

    @Test
    void shouldSaveProductSuccessfully() {

        Product producto = crearProductBase();

        Product savedProduct = productoRepository.save(producto);

        assertNotNull(savedProduct.getId());
        assertEquals("Laptop Gamer", savedProduct.getTitulo());
        assertEquals(3500.0, savedProduct.getPrecio());
    }

    @Test
    void shouldFindProductsByCategoryIdWhenCategoryExists() {

        Product producto = crearProductBase();

        Product savedProduct = productoRepository.save(producto);

        List<Product> productos =
                productoRepository.findByCategoryId(
                        savedProduct.getCategory().getId()
                );

        assertFalse(productos.isEmpty());
        assertEquals(1, productos.size());
    }

    @Test
    void shouldReturnEmptyListWhenCategoryDoesNotExist() {

        List<Product> productos =
                productoRepository.findByCategoryId(999L);

        assertTrue(productos.isEmpty());
    }

    @Test
    void shouldFindProductsByTipoWhenTipoExists() {

        Product producto = crearProductBase();

        productoRepository.save(producto);

        List<Product> productos =
                productoRepository.findByTipo(ProductType.VENTA);

        assertFalse(productos.isEmpty());
        assertEquals(ProductType.VENTA, productos.getFirst().getTipo());
    }

    @Test
    void shouldFindProductsByEstadoWhenEstadoExists() {

        Product producto = crearProductBase();

        productoRepository.save(producto);

        List<Product> productos =
                productoRepository.findByEstado(ProductStatus.DISPONIBLE);

        assertFalse(productos.isEmpty());
        assertEquals(
                ProductStatus.DISPONIBLE,
                productos.getFirst().getEstado()
        );
    }

    @Test
    void shouldFindProductsByVendedorIdWhenVendedorExists() {

        Product producto = crearProductBase();

        Product savedProduct = productoRepository.save(producto);

        Long vendedorId =
                savedProduct.getVendedor().getId();

        List<Product> productos =
                productoRepository.findByVendedorId(vendedorId);

        assertFalse(productos.isEmpty());
        assertEquals(1, productos.size());
    }
}