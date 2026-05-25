package com.example.proyecto_dbp.user;

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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtService jwtService;

    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setNombre("Juan Perez");
        userResponse.setEmail("juan@test.com");
        userResponse.setUniversidad("Universidad Catolica");
        userResponse.setFotoUrl("https://example.com/foto.jpg");
        userResponse.setRole(Role.USER);

        updateRequest = new UserUpdateRequest();
        updateRequest.setNombre("Juan Carlos Perez");
        updateRequest.setUniversidad("Universidad San Martin");
        updateRequest.setFotoUrl("https://example.com/nueva-foto.jpg");
    }

    @Test
    @DisplayName("should return user profile when getting /me")
    @WithMockUser(username = "juan@test.com", roles = {"USER"})
    void shouldReturnUserProfileWhenGetMe() throws Exception {
        when(userService.getMiPerfil()).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("should return user by id when exists")
    @WithMockUser
    void shouldReturnUserByIdWhenExists() throws Exception {
        when(userService.getPorId(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("should update profile when putting /me")
    @WithMockUser
    void shouldUpdateProfileWhenPutMe() throws Exception {
        when(userService.actualizarPerfil(any(UserUpdateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("should return no content when deleting /me")
    @WithMockUser
    void shouldReturnNoContentWhenDeleteMe() throws Exception {
        doNothing().when(userService).desactivarCuenta();

        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return all users when admin role")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnAllUsersWhenAdminRolee() throws Exception {
        UserResponse userResponse2 = new UserResponse();
        userResponse2.setEmail("maria@test.com");
        userResponse2.setNombre("Maria Lopez");
        userResponse2.setRole(Role.USER);

        List<UserResponse> users = Arrays.asList(userResponse, userResponse2);
        Page<UserResponse> page = new PageImpl<>(users);
        when(userService.getTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].email").value("juan@test.com"))
                .andExpect(jsonPath("$.content[1].email").value("maria@test.com"));
    }
}