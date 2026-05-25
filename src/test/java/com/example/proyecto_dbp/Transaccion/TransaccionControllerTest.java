package com.example.proyecto_dbp.Transaccion;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransaccionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Transaccion Controller Tests")
class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransaccionService transaccionService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private TransaccionRequestDTO requestDTO;
    private TransaccionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TransaccionRequestDTO();
        requestDTO.setProductoId(1L);
        requestDTO.setTipo(TipoTransaccion.COMPRA);

        responseDTO = new TransaccionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTipo(TipoTransaccion.COMPRA);
        responseDTO.setEstado(EstadoTransaccion.PENDIENTE);
        responseDTO.setMonto(1000.0);
        responseDTO.setCompradorNombre("Juan Perez");
        responseDTO.setVendedorNombre("Maria Lopez");
        responseDTO.setProductoTitulo("iPhone 14");
    }

    @Test
    @DisplayName("should create transaccion when POST /api/v1/transacciones")
    @WithMockUser
    void shouldCreateTransaccion() throws Exception {
        when(transaccionService.crear(any(TransaccionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/transacciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.monto").value(1000.0));
    }

    @Test
    @DisplayName("should return transaccion by id when GET /api/v1/transacciones/{id}")
    @WithMockUser
    void shouldReturnTransaccionById() throws Exception {
        when(transaccionService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/transacciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoTitulo").value("iPhone 14"));
    }

    @Test
    @DisplayName("should complete transaccion when PATCH /api/v1/transacciones/{id}/completar")
    @WithMockUser
    void shouldCompleteTransaccion() throws Exception {
        TransaccionResponseDTO completedResponse = responseDTO;
        completedResponse.setEstado(EstadoTransaccion.COMPLETADA);
        when(transaccionService.completar(1L)).thenReturn(completedResponse);

        mockMvc.perform(patch("/api/v1/transacciones/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    @DisplayName("should cancel transaccion when PATCH /api/v1/transacciones/{id}/cancelar")
    @WithMockUser
    void shouldCancelTransaccion() throws Exception {
        TransaccionResponseDTO cancelledResponse = responseDTO;
        cancelledResponse.setEstado(EstadoTransaccion.CANCELADA);
        when(transaccionService.cancelar(1L)).thenReturn(cancelledResponse);

        mockMvc.perform(patch("/api/v1/transacciones/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @DisplayName("should return mis compras when GET /api/v1/transacciones/mis-compras")
    @WithMockUser
    void shouldReturnMisCompras() throws Exception {
        List<TransaccionResponseDTO> compras = Arrays.asList(responseDTO);
        Page<TransaccionResponseDTO> page = new PageImpl<>(compras);
        when(transaccionService.misCompras(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/transacciones/mis-compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("should return mis ventas when GET /api/v1/transacciones/mis-ventas")
    @WithMockUser
    void shouldReturnMisVentas() throws Exception {
        List<TransaccionResponseDTO> ventas = Arrays.asList(responseDTO);
        Page<TransaccionResponseDTO> page = new PageImpl<>(ventas);
        when(transaccionService.misVentas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/transacciones/mis-ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}