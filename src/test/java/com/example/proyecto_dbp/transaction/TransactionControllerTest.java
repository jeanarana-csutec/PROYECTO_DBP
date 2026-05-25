package com.example.proyecto_dbp.Transaction;

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

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transaccionService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private TransactionRequest requestDTO;
    private TransactionResponse responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TransactionRequest();
        requestDTO.setProductId(1L);
        requestDTO.setTipo(TransactionType.COMPRA);

        responseDTO = new TransactionResponse();
        responseDTO.setId(1L);
        responseDTO.setTipo(TransactionType.COMPRA);
        responseDTO.setEstado(TransactionStatus.PENDIENTE);
        responseDTO.setMonto(1000.0);
        responseDTO.setCompradorNombre("Juan Perez");
        responseDTO.setVendedorNombre("Maria Lopez");
        responseDTO.setProductTitle("iPhone 14");
    }

    @Test
    @DisplayName("should create transaccion when POST /api/v1/transacciones")
    @WithMockUser
    void shouldCreateTransaction() throws Exception {
        when(transaccionService.crear(any(TransactionRequest.class))).thenReturn(responseDTO);

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
    void shouldReturnTransactionById() throws Exception {
        when(transaccionService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/transacciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productTitle").value("iPhone 14"));
    }

    @Test
    @DisplayName("should complete transaccion when PATCH /api/v1/transacciones/{id}/completar")
    @WithMockUser
    void shouldCompleteTransaction() throws Exception {
        TransactionResponse completedResponse = responseDTO;
        completedResponse.setEstado(TransactionStatus.COMPLETADA);
        when(transaccionService.completar(1L)).thenReturn(completedResponse);

        mockMvc.perform(patch("/api/v1/transacciones/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    @DisplayName("should cancel transaccion when PATCH /api/v1/transacciones/{id}/cancelar")
    @WithMockUser
    void shouldCancelTransaction() throws Exception {
        TransactionResponse cancelledResponse = responseDTO;
        cancelledResponse.setEstado(TransactionStatus.CANCELADA);
        when(transaccionService.cancelar(1L)).thenReturn(cancelledResponse);

        mockMvc.perform(patch("/api/v1/transacciones/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @DisplayName("should return mis compras when GET /api/v1/transacciones/mis-compras")
    @WithMockUser
    void shouldReturnMisCompras() throws Exception {
        List<TransactionResponse> compras = Arrays.asList(responseDTO);
        Page<TransactionResponse> page = new PageImpl<>(compras);
        when(transaccionService.misCompras(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/transacciones/mis-compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("should return mis ventas when GET /api/v1/transacciones/mis-ventas")
    @WithMockUser
    void shouldReturnMisVentas() throws Exception {
        List<TransactionResponse> ventas = Arrays.asList(responseDTO);
        Page<TransactionResponse> page = new PageImpl<>(ventas);
        when(transaccionService.misVentas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/transacciones/mis-ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}