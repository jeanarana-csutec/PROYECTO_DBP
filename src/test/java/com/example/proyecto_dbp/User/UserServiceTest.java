package com.example.proyecto_dbp.User;

import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import com.example.proyecto_dbp.Security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNombre("Juan Perez");
        user.setEmail("juan@test.com");
        user.setPassword("encodedPassword");
        user.setRol(Rol.USER);
        user.setUniversidad("Universidad Catolica");
        user.setFotoUrl("https://example.com/foto.jpg");
        user.setFechaRegistro(LocalDateTime.now());
        user.setActivo(true);

        userResponse = new UserResponse();
        userResponse.setNombre("Juan Perez");
        userResponse.setEmail("juan@test.com");
        userResponse.setUniversidad("Universidad Catolica");
        userResponse.setFotoUrl("https://example.com/foto.jpg");
        userResponse.setRol(Rol.USER);

        updateRequest = new UserUpdateRequestDTO();
        updateRequest.setNombre("Juan Carlos Perez");
        updateRequest.setUniversidad("Universidad San Martin");
        updateRequest.setFotoUrl("https://example.com/nueva-foto.jpg");
    }

    @Test
    @DisplayName("should load user by username when email exists")
    void shouldLoadUserByUsernameWhenEmailExists() {
        // Given
        when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userService.loadUserByUsername("juan@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("juan@test.com");
        assertThat(userDetails.isEnabled()).isTrue();
        verify(userRepository).findByEmail("juan@test.com");
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException when email does not exist")
    void shouldThrowUsernameNotFoundExceptionWhenEmailNotExists() {
        // Given
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.loadUserByUsername("noexiste@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("should get current user profile when authenticated")
    void shouldGetMiPerfilWhenUserAuthenticated() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // When
        UserResponse result = userService.getMiPerfil();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        assertThat(result.getNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("should throw ResourceNotFound when getting profile of non-existent user")
    void shouldThrowResourceNotFoundWhenUserNotExistsInGetMiPerfil() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenThrow(new ResourceNotFound("Usuario no encontrado"));

        // When/Then
        assertThatThrownBy(() -> userService.getMiPerfil())
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("should get user by id when exists")
    void shouldGetPorIdWhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // When
        UserResponse result = userService.getPorId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFound when getting user by non-existent id")
    void shouldThrowResourceNotFoundWhenUserNotExistsInGetPorId() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getPorId(999L))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Usuario no encontrado con id: 999");
    }

    @Test
    @DisplayName("should update profile when valid data provided")
    void shouldActualizarPerfilWhenValidData() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // When
        UserResponse result = userService.actualizarPerfil(updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
        assertThat(user.getNombre()).isEqualTo("Juan Carlos Perez");
        assertThat(user.getUniversidad()).isEqualTo("Universidad San Martin");
        assertThat(user.getFotoUrl()).isEqualTo("https://example.com/nueva-foto.jpg");
    }

    @Test
    @DisplayName("should only update non-null fields in profile")
    void shouldOnlyUpdateNonNullFields() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        updateRequest.setNombre(null);
        updateRequest.setFotoUrl(null);

        // When
        userService.actualizarPerfil(updateRequest);

        // Then
        assertThat(user.getNombre()).isEqualTo("Juan Perez"); // No cambió
        assertThat(user.getFotoUrl()).isEqualTo("https://example.com/foto.jpg"); // No cambió
        assertThat(user.getUniversidad()).isEqualTo("Universidad San Martin"); // Sí cambió
    }

    @Test
    @DisplayName("should deactivate account when user authenticated")
    void shouldDesactivarCuentaWhenUserAuthenticated() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.desactivarCuenta();

        // Then
        assertThat(user.getActivo()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should get all users when admin")
    void shouldGetTodosWhenAdmin() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("maria@test.com");
        user2.setNombre("Maria Lopez");

        Page<User> page = new PageImpl<>(List.of(user, user2));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setEmail("maria@test.com");
        when(modelMapper.map(user2, UserResponse.class)).thenReturn(userResponse2);

        // When
        Page<UserResponse> results = userService.getTodos(Pageable.unpaged());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.getContent().get(0).getEmail()).isEqualTo("juan@test.com");
        assertThat(results.getContent().get(1).getEmail()).isEqualTo("maria@test.com");
        verify(userRepository).findAll(any(Pageable.class));
    }

    private void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("juan@test.com");
        SecurityContextHolder.setContext(securityContext);
    }
}