package com.example.proyecto_dbp.TestDTOs;

import com.example.proyecto_dbp.Auth.AuthLoginRequest;
import com.example.proyecto_dbp.Auth.AuthLoginResponse;
import com.example.proyecto_dbp.Auth.AuthRefreshRequest;
import com.example.proyecto_dbp.Auth.AuthRequest;
import com.example.proyecto_dbp.Auth.AuthResponse;
import com.example.proyecto_dbp.Categoria.CategoriaRequestDTO;
import com.example.proyecto_dbp.Categoria.CategoriaResponseDTO;
import com.example.proyecto_dbp.Favorito.FavoritoRequestDTO;
import com.example.proyecto_dbp.Favorito.FavoritoResponseDTO;
import com.example.proyecto_dbp.Mensaje.MensajeRequestDTO;
import com.example.proyecto_dbp.Mensaje.MensajeResponseDTO;
import com.example.proyecto_dbp.Producto.ProductoRequestDTO;
import com.example.proyecto_dbp.Producto.ProductoResponseDTO;
import com.example.proyecto_dbp.Producto.TipoProducto;
import com.example.proyecto_dbp.Resena.ResenaRequestDTO;
import com.example.proyecto_dbp.Resena.ResenaResponseDTO;
import com.example.proyecto_dbp.Transaccion.TipoTransaccion;
import com.example.proyecto_dbp.Transaccion.TransaccionRequestDTO;
import com.example.proyecto_dbp.Transaccion.TransaccionResponseDTO;
import com.example.proyecto_dbp.User.UserResponse;
import com.example.proyecto_dbp.User.UserUpdateRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("DTOs Validation Tests")
class DTOsValidationTest {

    @Autowired
    private JacksonTester<AuthLoginRequest> authLoginRequestTester;
    @Autowired
    private JacksonTester<AuthLoginResponse> authLoginResponseTester;
    @Autowired
    private JacksonTester<AuthRefreshRequest> authRefreshRequestTester;
    @Autowired
    private JacksonTester<AuthRequest> authRequestTester;
    @Autowired
    private JacksonTester<AuthResponse> authResponseTester;
    @Autowired
    private JacksonTester<UserUpdateRequestDTO> userUpdateRequestTester;
    @Autowired
    private JacksonTester<UserResponse> userResponseTester;
    @Autowired
    private JacksonTester<ProductoRequestDTO> productoRequestTester;
    @Autowired
    private JacksonTester<ProductoResponseDTO> productoResponseTester;
    @Autowired
    private JacksonTester<CategoriaRequestDTO> categoriaRequestTester;
    @Autowired
    private JacksonTester<CategoriaResponseDTO> categoriaResponseTester;
    @Autowired
    private JacksonTester<TransaccionRequestDTO> transaccionRequestTester;
    @Autowired
    private JacksonTester<TransaccionResponseDTO> transaccionResponseTester;
    @Autowired
    private JacksonTester<FavoritoRequestDTO> favoritoRequestTester;
    @Autowired
    private JacksonTester<FavoritoResponseDTO> favoritoResponseTester;
    @Autowired
    private JacksonTester<ResenaRequestDTO> resenaRequestTester;
    @Autowired
    private JacksonTester<ResenaResponseDTO> resenaResponseTester;
    @Autowired
    private JacksonTester<MensajeRequestDTO> mensajeRequestTester;
    @Autowired
    private JacksonTester<MensajeResponseDTO> mensajeResponseTester;

    // ========== AUTH DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize AuthLoginRequest")
    void testAuthLoginRequest() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        JsonContent<AuthLoginRequest> json = authLoginRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
        assertThat(json).extractingJsonPathStringValue("$.password").isEqualTo("password123");

        AuthLoginRequest deserialized = authLoginRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("should serialize and deserialize AuthRequest")
    void testAuthRequest() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        JsonContent<AuthRequest> json = authRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
        assertThat(json).extractingJsonPathStringValue("$.password").isEqualTo("password123");

        AuthRequest deserialized = authRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("should serialize and deserialize AuthResponse")
    void testAuthResponse() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("jwt-token-123");
        response.setRefreshToken("refresh-token-456");
        response.setNombre("Test User");
        response.setUniversidad("Test University");
        response.setFechaRegistro(LocalDateTime.now());

        JsonContent<AuthResponse> json = authResponseTester.write(response);

        assertThat(json).extractingJsonPathStringValue("$.token").isEqualTo("jwt-token-123");
        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.universidad").isEqualTo("Test University");

        AuthResponse deserialized = authResponseTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getToken()).isEqualTo("jwt-token-123");
        assertThat(deserialized.getNombre()).isEqualTo("Test User");
    }

    // ========== USER DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize UserUpdateRequestDTO")
    void testUserUpdateRequest() throws Exception {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setNombre("Updated Name");
        request.setFotoUrl("https://example.com/photo.jpg");
        request.setUniversidad("Updated University");

        JsonContent<UserUpdateRequestDTO> json = userUpdateRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Updated Name");
        assertThat(json).extractingJsonPathStringValue("$.fotoUrl").isEqualTo("https://example.com/photo.jpg");
        assertThat(json).extractingJsonPathStringValue("$.universidad").isEqualTo("Updated University");

        UserUpdateRequestDTO deserialized = userUpdateRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getNombre()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("should serialize and deserialize UserResponse")
    void testUserResponse() throws Exception {
        UserResponse response = new UserResponse();
        response.setNombre("Test User");
        response.setEmail("test@test.com");
        response.setUniversidad("Test University");

        JsonContent<UserResponse> json = userResponseTester.write(response);

        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Test User");

        UserResponse deserialized = userResponseTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getEmail()).isEqualTo("test@test.com");
    }

    // ========== PRODUCTO DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize ProductoRequestDTO")
    void testProductoRequest() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTitulo("iPhone 14");
        request.setDescripcion("Smartphone Apple");
        request.setPrecio(1000.0);
        request.setTipo(TipoProducto.VENTA);
        request.setCategoriaId(1L);

        JsonContent<ProductoRequestDTO> json = productoRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.titulo").isEqualTo("iPhone 14");
        assertThat(json).extractingJsonPathNumberValue("$.precio").isEqualTo(1000.0);

        ProductoRequestDTO deserialized = productoRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getTitulo()).isEqualTo("iPhone 14");
    }

    // ========== CATEGORIA DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize CategoriaRequestDTO")
    void testCategoriaRequest() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Electrónica");
        request.setDescripcion("Productos electrónicos");

        JsonContent<CategoriaRequestDTO> json = categoriaRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Electrónica");

        CategoriaRequestDTO deserialized = categoriaRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getNombre()).isEqualTo("Electrónica");
    }

    @Test
    @DisplayName("should serialize and deserialize CategoriaResponseDTO")
    void testCategoriaResponse() throws Exception {
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(1L);
        response.setNombre("Electrónica");
        response.setDescripcion("Productos electrónicos");

        JsonContent<CategoriaResponseDTO> json = categoriaResponseTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Electrónica");
    }

    // ========== TRANSACCION DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize TransaccionRequestDTO")
    void testTransaccionRequest() throws Exception {
        TransaccionRequestDTO request = new TransaccionRequestDTO();
        request.setProductoId(1L);
        request.setTipo(TipoTransaccion.COMPRA);

        JsonContent<TransaccionRequestDTO> json = transaccionRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.productoId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.tipo").isEqualTo("COMPRA");

        TransaccionRequestDTO deserialized = transaccionRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getProductoId()).isEqualTo(1L);
    }

    // ========== FAVORITO DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize FavoritoRequestDTO")
    void testFavoritoRequest() throws Exception {
        FavoritoRequestDTO request = new FavoritoRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(2L);

        JsonContent<FavoritoRequestDTO> json = favoritoRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.usuarioId").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.productoId").isEqualTo(2);

        FavoritoRequestDTO deserialized = favoritoRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should serialize and deserialize FavoritoResponseDTO")
    void testFavoritoResponse() throws Exception {
        FavoritoResponseDTO response = new FavoritoResponseDTO();
        response.setId(1L);
        response.setProductoId(2L);
        response.setProductoTitulo("iPhone 14");
        response.setProductoPrecio(1000.0);
        response.setFechaAgregado(LocalDateTime.now());

        JsonContent<FavoritoResponseDTO> json = favoritoResponseTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.productoId").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.productoTitulo").isEqualTo("iPhone 14");

        FavoritoResponseDTO deserialized = favoritoResponseTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getProductoTitulo()).isEqualTo("iPhone 14");
    }

    // ========== RESENA DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize ResenaRequestDTO")
    void testResenaRequest() throws Exception {
        ResenaRequestDTO request = new ResenaRequestDTO();
        request.setPuntuacion(5);
        request.setComentario("Excelente producto");
        request.setTransaccionId(1L);

        JsonContent<ResenaRequestDTO> json = resenaRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.puntuacion").isEqualTo(5);
        assertThat(json).extractingJsonPathStringValue("$.comentario").isEqualTo("Excelente producto");

        ResenaRequestDTO deserialized = resenaRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getPuntuacion()).isEqualTo(5);
    }

    // ========== MENSAJE DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize MensajeRequestDTO")
    void testMensajeRequest() throws Exception {
        MensajeRequestDTO request = new MensajeRequestDTO();
        request.setReceptorId(1L);
        request.setProductoId(2L);
        request.setContenido("Hola, ¿está disponible?");

        JsonContent<MensajeRequestDTO> json = mensajeRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.receptorId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.contenido").isEqualTo("Hola, ¿está disponible?");

        MensajeRequestDTO deserialized = mensajeRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getContenido()).isEqualTo("Hola, ¿está disponible?");
    }
}