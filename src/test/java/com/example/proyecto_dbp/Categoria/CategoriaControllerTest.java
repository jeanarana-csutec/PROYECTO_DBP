package com.example.proyecto_dbp.Categoria;

import com.example.proyecto_dbp.Security.JwtAuthorizationFilter;
import com.example.proyecto_dbp.Security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(CategoriaController.class)
@DisplayName("Categoria Controller Tests")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private CategoriaResponseDTO responseDTO;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new CategoriaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre("Electrónica");
        responseDTO.setDescripcion("Productos electrónicos y tecnología");

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Electrónica");
        requestDTO.setDescripcion("Productos electrónicos y tecnología");
    }

    @Test
    @DisplayName("should return all categorias when GET /api/v1/categorias")
    @WithMockUser
    void shouldReturnAllCategorias() throws Exception {
        // Given
        CategoriaResponseDTO responseDTO2 = new CategoriaResponseDTO();
        responseDTO2.setId(2L);
        responseDTO2.setNombre("Hogar");
        responseDTO2.setDescripcion("Productos para el hogar");

        List<CategoriaResponseDTO> categorias = Arrays.asList(responseDTO, responseDTO2);
        when(categoriaService.obtenerTodas()).thenReturn(categorias);

        // When & Then
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Electrónica"))
                .andExpect(jsonPath("$[1].nombre").value("Hogar"));
    }

    @Test
    @DisplayName("should return categoria by id when GET /api/v1/categorias/{id}")
    @WithMockUser
    void shouldReturnCategoriaById() throws Exception {
        // Given
        when(categoriaService.obtenerPorId(1L)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónica"))
                .andExpect(jsonPath("$.descripcion").value("Productos electrónicos y tecnología"));
    }

    @Test
    @DisplayName("should return 404 when categoria not found")
    @WithMockUser
    void shouldReturn404WhenCategoriaNotFound() throws Exception {
        // Given
        when(categoriaService.obtenerPorId(999L))
                .thenThrow(new RuntimeException("Categoría no encontrada con id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/categorias/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("should create categoria when POST /api/v1/categorias")
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateCategoria() throws Exception {
        // Given
        when(categoriaService.crear(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónica"));
    }

    @Test
    @DisplayName("should return 400 when creating categoria with invalid data")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn400WhenCreatingCategoriaWithInvalidData() throws Exception {
        // Given
        CategoriaRequestDTO invalidRequest = new CategoriaRequestDTO();
        invalidRequest.setNombre(""); // Nombre vacío - inválido

        // When & Then
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update categoria when PUT /api/v1/categorias/{id}")
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateCategoria() throws Exception {
        // Given
        when(categoriaService.actualizar(eq(1L), any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónica"));
    }

    @Test
    @DisplayName("should delete categoria when DELETE /api/v1/categorias/{id}")
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteCategoria() throws Exception {
        // Given
        doNothing().when(categoriaService).eliminar(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 403 when non-admin tries to create categoria")
    @WithMockUser(roles = {"USER"})
    void shouldReturn403WhenNonAdminCreatesCategoria() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 403 when non-admin tries to update categoria")
    @WithMockUser(roles = {"USER"})
    void shouldReturn403WhenNonAdminUpdatesCategoria() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 403 when non-admin tries to delete categoria")
    @WithMockUser(roles = {"USER"})
    void shouldReturn403WhenNonAdminDeletesCategoria() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isForbidden());
    }
}