package com.example.proyecto_dbp.Category;

import com.example.proyecto_dbp.exception.GlobalExceptionHandler;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Category Controller Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoriaService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private CategoryResponse responseDTO;
    private CategoryRequest requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new CategoryResponse();
        responseDTO.setId(1L);
        responseDTO.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        responseDTO.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos y tecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");

        requestDTO = new CategoryRequest();
        requestDTO.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        requestDTO.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos y tecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");
    }

    @Test
    @DisplayName("should return all categorias when GET /api/v1/categorias")
    @WithMockUser
    void shouldReturnAllCategorys() throws Exception {
        // Given
        CategoryResponse responseDTO2 = new CategoryResponse();
        responseDTO2.setId(2L);
        responseDTO2.setNombre("Hogar");
        responseDTO2.setDescripcion("Products para el hogar");

        List<CategoryResponse> categorias = Arrays.asList(responseDTO, responseDTO2);
        when(categoriaService.obtenerTodas()).thenReturn(categorias);

        // When & Then
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica"))
                .andExpect(jsonPath("$[1].nombre").value("Hogar"));
    }

    @Test
    @DisplayName("should return categoria by id when GET /api/v1/categorias/{id}")
    @WithMockUser
    void shouldReturnCategoryById() throws Exception {
        // Given
        when(categoriaService.obtenerPorId(1L)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica"))
                .andExpect(jsonPath("$.descripcion").value("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos y tecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a"));
    }

    @Test
    @DisplayName("should return 404 when categoria not found")
    @WithMockUser
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        // Given
        when(categoriaService.obtenerPorId(999L))
                .thenThrow(new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/categorias/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should create categoria when POST /api/v1/categorias")
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateCategory() throws Exception {
        // Given
        when(categoriaService.crear(any(CategoryRequest.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica"));
    }

    @Test
    @DisplayName("should return 400 when creating categoria with invalid data")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn400WhenCreatingCategoryWithInvalidData() throws Exception {
        // Given
        CategoryRequest invalidRequest = new CategoryRequest();
        invalidRequest.setNombre(""); // Nombre vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­o - invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido

        // When & Then
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update categoria when PUT /api/v1/categorias/{id}")
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateCategory() throws Exception {
        // Given
        when(categoriaService.actualizar(eq(1L), any(CategoryRequest.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica"));
    }

    @Test
    @DisplayName("should delete categoria when DELETE /api/v1/categorias/{id}")
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteCategory() throws Exception {
        // Given
        doNothing().when(categoriaService).eliminar(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());
    }
}