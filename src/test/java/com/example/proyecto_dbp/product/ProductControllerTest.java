package com.example.proyecto_dbp.Product;

import com.example.proyecto_dbp.exception.GlobalExceptionHandler;
import com.example.proyecto_dbp.security.JwtAuthorizationFilter;
import com.example.proyecto_dbp.security.JwtService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productoService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private ProductResponse responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ProductResponse();
        responseDTO.setId(1L);
        responseDTO.setTitulo("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo");
        responseDTO.setDescripcion("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo avanzado");
        responseDTO.setPrecio(50.0);
        responseDTO.setTipo(ProductType.VENTA);
        responseDTO.setEstado(ProductStatus.DISPONIBLE);
        responseDTO.setFechaPublicacion(LocalDateTime.now());
        responseDTO.setVendedorNombre("Juan Perez");
        responseDTO.setCategoryName("MatemÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ticas");
    }

    @Test
    @DisplayName("should return all products when GET /api/v1/productos")
    void shouldReturnAllProducts() throws Exception {
        ProductResponse dto2 = new ProductResponse();
        dto2.setId(2L);
        dto2.setTitulo("Laptop");

        List<ProductResponse> productos = Arrays.asList(responseDTO, dto2);
        Page<ProductResponse> page = new PageImpl<>(productos);
        when(productoService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }

    @Test
    @DisplayName("should return product by id when GET /api/v1/productos/{id}")
    void shouldReturnProductById() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }

    @Test
    @DisplayName("should create product when POST /api/v1/productos")
    void shouldCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setTitulo("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo");
        request.setDescripcion("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo avanzado");
        request.setPrecio(50.0);
        request.setTipo(ProductType.VENTA);
        request.setCategoryId(1L);

        when(productoService.crearProduct(any(ProductRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }

    @Test
    @DisplayName("should update product when PUT /api/v1/productos/{id}")
    void shouldUpdateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setTitulo("Libro actualizado");
        request.setPrecio(60.0);
        request.setTipo(ProductType.VENTA);
        request.setCategoryId(1L);

        ProductResponse updated = new ProductResponse();
        updated.setId(1L);
        updated.setTitulo("Libro actualizado");
        updated.setPrecio(60.0);
        updated.setTipo(ProductType.VENTA);

        when(productoService.actualizarProduct(eq(1L), any(ProductRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Libro actualizado"));
    }

    @Test
    @DisplayName("should return 400 when create product with invalid data")
    void shouldReturn400WhenCreateProductWithInvalidData() throws Exception {
        ProductRequest request = new ProductRequest();

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should filter products by categoria when GET /api/v1/productos/categoria/{id}")
    void shouldFilterByCategory() throws Exception {
        when(productoService.filtrarPorCategory(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }

    @Test
    @DisplayName("should filter products by estado when GET /api/v1/productos/estado/{estado}")
    void shouldFilterByEstado() throws Exception {
        when(productoService.filtrarPorEstado(ProductStatus.DISPONIBLE)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/estado/DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }

    @Test
    @DisplayName("should filter products by tipo when GET /api/v1/productos/tipo/{tipo}")
    void shouldFilterByTipo() throws Exception {
        when(productoService.filtrarPorTipo(ProductType.VENTA)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/tipo/VENTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo"));
    }
}
