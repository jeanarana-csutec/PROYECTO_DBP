package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Categoria.Categoria;
import com.example.proyecto_dbp.Categoria.CategoriaRepository;
import com.example.proyecto_dbp.Exceptions.Forbidden;
import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
import com.example.proyecto_dbp.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;
    private SecurityUtils securityUtils;
    private ModelMapper modelMapper;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoRepository = mock(ProductoRepository.class);
        categoriaRepository = mock(CategoriaRepository.class);
        securityUtils = mock(SecurityUtils.class);
        modelMapper = new ModelMapper();

        productoService = new ProductoService(
                modelMapper,
                productoRepository,
                categoriaRepository,
                securityUtils
        );
    }

    // ===== MÉTODO AUXILIAR =====
    private User crearUsuario() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Itta");
        user.setEmail("itta@utp.edu.pe");
        user.setRol(Rol.USER);
        return user;
    }

    private Categoria crearCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Tecnología");
        return categoria;
    }

    private Producto crearProducto(User vendedor, Categoria categoria) {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setTitulo("Laptop");
        producto.setPrecio(3000.0);
        producto.setTipo(TipoProducto.VENTA);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria);
        return producto;
    }

    @Test
    void shouldCreateProductSuccessfullyWhenDataIsValid() {

        User user = crearUsuario();
        Categoria categoria = crearCategoria();

        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTitulo("Laptop");
        request.setDescripcion("RTX");
        request.setPrecio(3000.0);
        request.setTipo(TipoProducto.VENTA);
        request.setCategoriaId(1L);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(i -> i.getArgument(0));

        ProductoResponseDTO response =
                productoService.crearProducto(request);

        assertNotNull(response);
        assertEquals("Laptop", response.getTitulo());

        verify(productoRepository, times(1))
                .save(any(Producto.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoriaDoesNotExist() {

        User user = crearUsuario();

        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCategoriaId(99L);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFound.class,
                () -> productoService.crearProducto(request)
        );
    }

    @Test
    void shouldReturnAllProductsSuccessfully() {

        User user = crearUsuario();
        Categoria categoria = crearCategoria();

        Producto producto = crearProducto(user, categoria);

        when(productoRepository.findAll())
                .thenReturn(List.of(producto));

        List<ProductoResponseDTO> productos =
                productoService.obtenerTodos();

        assertEquals(1, productos.size());
        assertEquals("Laptop", productos.getFirst().getTitulo());
    }

    @Test
    void shouldReturnProductByIdWhenProductExists() {

        User user = crearUsuario();
        Categoria categoria = crearCategoria();

        Producto producto = crearProducto(user, categoria);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        ProductoResponseDTO response =
                productoService.obtenerPorId(1L);

        assertEquals("Laptop", response.getTitulo());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFound.class,
                () -> productoService.obtenerPorId(999L)
        );
    }

    @Test
    void shouldDeleteProductWhenUserIsOwner() {

        User user = crearUsuario();
        Categoria categoria = crearCategoria();

        Producto producto = crearProducto(user, categoria);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void shouldThrowForbiddenWhenUserIsNotOwner() {

        User owner = crearUsuario();

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("otro@utp.edu.pe");
        anotherUser.setRol(Rol.USER);

        Categoria categoria = crearCategoria();

        Producto producto =
                crearProducto(owner, categoria);

        when(securityUtils.getUsuarioAutenticado()).thenReturn(anotherUser);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                Forbidden.class,
                () -> productoService.eliminarProducto(1L)
        );
    }
}