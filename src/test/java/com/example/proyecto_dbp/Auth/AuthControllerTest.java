package com.example.proyecto_dbp.Auth;

import com.example.proyecto_dbp.Exceptions.GlobalExceptionHandler;
import com.example.proyecto_dbp.Security.JwtAuthorizationFilter;
import com.example.proyecto_dbp.Security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("should register user when POST /api/v1/auth/register")
    void shouldRegisterUser() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@unsa.edu.pe");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("token1", "refresh1", "Test User", LocalDateTime.now(), "unsa");

        when(authService.registro(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token1"))
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    @DisplayName("should login user when POST /api/v1/auth/login")
    void shouldLoginUser() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("test@unsa.edu.pe");
        request.setPassword("password123");

        AuthLoginResponse response = new AuthLoginResponse("access-token", "refresh-token");

        when(authService.login(any(AuthLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("should refresh token when POST /api/v1/auth/refresh")
    void shouldRefreshToken() throws Exception {
        AuthRefreshRequest request = new AuthRefreshRequest();
        request.setRefreshToken("old-refresh-token");

        AuthLoginResponse response = new AuthLoginResponse("new-access", "new-refresh");

        when(authService.refresh("old-refresh-token")).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    @DisplayName("should return 400 when register with invalid email")
    void shouldReturn400WhenRegisterWithInvalidEmail() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when register with short password")
    void shouldReturn400WhenRegisterWithShortPassword() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@unsa.edu.pe");
        request.setPassword("123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
