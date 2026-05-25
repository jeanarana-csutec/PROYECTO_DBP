package com.example.proyecto_dbp.Producto;

import com.example.proyecto_dbp.Exceptions.GlobalExceptionHandler;
import com.example.proyecto_dbp.Security.JwtAuthorizationFilter;
import com.example.proyecto_dbp.Security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Producto Controller Tests")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ProductoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitulo("Libro de cálculo");
        responseDTO.setDescripcion("Libro de cálculo avanzado");
        responseDTO.setPrecio(50.0);
        responseDTO.setTipo(TipoProducto.VENTA);
        responseDTO.setEstado(EstadoProducto.DISPONIBLE);
        responseDTO.setFechaPublicacion(LocalDateTime.now());
        responseDTO.setVendedorNombre("Juan Perez");
        responseDTO.setCategoriaNombre("Matemáticas");
    }

    @Test
    @DisplayName("should return all products when GET /api/v1/productos")
    void shouldReturnAllProducts() throws Exception {
        ProductoResponseDTO dto2 = new ProductoResponseDTO();
        dto2.setId(2L);
        dto2.setTitulo("Laptop");

        List<ProductoResponseDTO> productos = Arrays.asList(responseDTO, dto2);
        when(productoService.obtenerTodos()).thenReturn(productos);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Libro de cálculo"));
    }

    @Test
    @DisplayName("should return product by id when GET /api/v1/productos/{id}")
    void shouldReturnProductById() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Libro de cálculo"));
    }

    @Test
    @DisplayName("should create product when POST /api/v1/productos")
    void shouldCreateProduct() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTitulo("Libro de cálculo");
        request.setDescripcion("Libro de cálculo avanzado");
        request.setPrecio(50.0);
        request.setTipo(TipoProducto.VENTA);
        request.setCategoriaId(1L);

        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Libro de cálculo"));
    }

    @Test
    @DisplayName("should update product when PUT /api/v1/productos/{id}")
    void shouldUpdateProduct() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTitulo("Libro actualizado");
        request.setPrecio(60.0);
        request.setTipo(TipoProducto.VENTA);
        request.setCategoriaId(1L);

        ProductoResponseDTO updated = new ProductoResponseDTO();
        updated.setId(1L);
        updated.setTitulo("Libro actualizado");
        updated.setPrecio(60.0);
        updated.setTipo(TipoProducto.VENTA);

        when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Libro actualizado"));
    }

    @Test
    @DisplayName("should return 400 when create product with invalid data")
    void shouldReturn400WhenCreateProductWithInvalidData() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should filter products by categoria when GET /api/v1/productos/categoria/{id}")
    void shouldFilterByCategoria() throws Exception {
        when(productoService.filtrarPorCategoria(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cálculo"));
    }

    @Test
    @DisplayName("should filter products by estado when GET /api/v1/productos/estado/{estado}")
    void shouldFilterByEstado() throws Exception {
        when(productoService.filtrarPorEstado(EstadoProducto.DISPONIBLE)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/estado/DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cálculo"));
    }

    @Test
    @DisplayName("should filter products by tipo when GET /api/v1/productos/tipo/{tipo}")
    void shouldFilterByTipo() throws Exception {
        when(productoService.filtrarPorTipo(TipoProducto.VENTA)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/tipo/VENTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cálculo"));
    }
}
