package com.example.proyecto_dbp.Product;

import com.example.proyecto_dbp.Category.Category;
import com.example.proyecto_dbp.Category.CategoryRepository;
import com.example.proyecto_dbp.exception.ForbiddenException;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.Role;
import com.example.proyecto_dbp.user.User;
import com.example.proyecto_dbp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productoRepository;
    private CategoryRepository categoriaRepository;
    private SecurityUtils securityUtils;
    private ModelMapper modelMapper;

    private ProductService productoService;

    @BeforeEach
    void setUp() {
        productoRepository = mock(ProductRepository.class);
        categoriaRepository = mock(CategoryRepository.class);
        securityUtils = mock(SecurityUtils.class);
        modelMapper = new ModelMapper();

        productoService = new ProductService(
                modelMapper,
                productoRepository,
                categoriaRepository,
                securityUtils
        );
    }


    private User crearUsuario() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Itta");
        user.setEmail("itta@utp.edu.pe");
        user.setRole(Role.USER);
        return user;
    }

    private Category crearCategory() {
        Category categoria = new Category();
        categoria.setId(1L);
        categoria.setNombre("TecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");
        return categoria;
    }

    private Product crearProduct(User vendedor, Category categoria) {
        Product producto = new Product();
        producto.setId(1L);
        producto.setTitulo("Laptop");
        producto.setPrecio(3000.0);
        producto.setTipo(ProductType.VENTA);
        producto.setEstado(ProductStatus.DISPONIBLE);
        producto.setVendedor(vendedor);
        producto.setCategory(categoria);
        return producto;
    }

    @Test
    void shouldCreateProductSuccessfullyWhenDataIsValid() {

        User user = crearUsuario();
        Category categoria = crearCategory();

        ProductRequest request = new ProductRequest();
        request.setTitulo("Laptop");
        request.setDescripcion("RTX");
        request.setPrecio(3000.0);
        request.setTipo(ProductType.VENTA);
        request.setCategoryId(1L);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(productoRepository.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        ProductResponse response =
                productoService.crearProduct(request);

        assertNotNull(response);
        assertEquals("Laptop", response.getTitulo());

        verify(productoRepository, times(1))
                .save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoryDoesNotExist() {

        User user = crearUsuario();

        ProductRequest request = new ProductRequest();
        request.setCategoryId(99L);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.crearProduct(request)
        );
    }

    @Test
    void shouldReturnAllProductsSuccessfully() {

        User user = crearUsuario();
        Category categoria = crearCategory();

        Product producto = crearProduct(user, categoria);

        Page<Product> page = new PageImpl<>(List.of(producto));
        when(productoRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<ProductResponse> productos =
                productoService.obtenerTodos(Pageable.unpaged());

        assertEquals(1, productos.getContent().size());
        assertEquals("Laptop", productos.getContent().getFirst().getTitulo());
    }

    @Test
    void shouldReturnProductByIdWhenProductExists() {

        User user = crearUsuario();
        Category categoria = crearCategory();

        Product producto = crearProduct(user, categoria);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        ProductResponse response =
                productoService.obtenerPorId(1L);

        assertEquals("Laptop", response.getTitulo());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.obtenerPorId(999L)
        );
    }

    @Test
    void shouldDeleteProductWhenUserIsOwner() {

        User user = crearUsuario();
        Category categoria = crearCategory();

        Product producto = crearProduct(user, categoria);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        productoService.eliminarProduct(1L);

        verify(productoRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUserIsNotOwner() {

        User owner = crearUsuario();

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("otro@utp.edu.pe");
        anotherUser.setRole(Role.USER);

        Category categoria = crearCategory();

        Product producto =
                crearProduct(owner, categoria);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(anotherUser);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                ForbiddenException.class,
                () -> productoService.eliminarProduct(1L)
        );
    }
}