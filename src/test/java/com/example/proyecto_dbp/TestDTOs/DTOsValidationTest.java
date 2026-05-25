package com.example.proyecto_dbp.TestDTOs;

import com.example.proyecto_dbp.auth.LoginRequest;
import com.example.proyecto_dbp.auth.LoginResponse;
import com.example.proyecto_dbp.auth.RefreshTokenRequest;
import com.example.proyecto_dbp.auth.RegisterRequest;
import com.example.proyecto_dbp.auth.RegisterResponse;
import com.example.proyecto_dbp.Category.CategoryRequest;
import com.example.proyecto_dbp.Category.CategoryResponse;
import com.example.proyecto_dbp.Favorite.FavoriteRequest;
import com.example.proyecto_dbp.Favorite.FavoriteResponse;
import com.example.proyecto_dbp.Message.MessageRequest;
import com.example.proyecto_dbp.Message.MessageResponse;
import com.example.proyecto_dbp.Product.ProductRequest;
import com.example.proyecto_dbp.Product.ProductResponse;
import com.example.proyecto_dbp.Product.ProductType;
import com.example.proyecto_dbp.Review.ReviewRequest;
import com.example.proyecto_dbp.Review.ReviewResponse;
import com.example.proyecto_dbp.Transaction.TransactionType;
import com.example.proyecto_dbp.Transaction.TransactionRequest;
import com.example.proyecto_dbp.Transaction.TransactionResponse;
import com.example.proyecto_dbp.user.UserResponse;
import com.example.proyecto_dbp.user.UserUpdateRequest;
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
    private JacksonTester<LoginRequest> authLoginRequestTester;
    @Autowired
    private JacksonTester<LoginResponse> authLoginResponseTester;
    @Autowired
    private JacksonTester<RefreshTokenRequest> authRefreshRequestTester;
    @Autowired
    private JacksonTester<RegisterRequest> authRequestTester;
    @Autowired
    private JacksonTester<RegisterResponse> authResponseTester;
    @Autowired
    private JacksonTester<UserUpdateRequest> userUpdateRequestTester;
    @Autowired
    private JacksonTester<UserResponse> userResponseTester;
    @Autowired
    private JacksonTester<ProductRequest> productoRequestTester;
    @Autowired
    private JacksonTester<ProductResponse> productoResponseTester;
    @Autowired
    private JacksonTester<CategoryRequest> categoriaRequestTester;
    @Autowired
    private JacksonTester<CategoryResponse> categoriaResponseTester;
    @Autowired
    private JacksonTester<TransactionRequest> transaccionRequestTester;
    @Autowired
    private JacksonTester<TransactionResponse> transaccionResponseTester;
    @Autowired
    private JacksonTester<FavoriteRequest> favoritoRequestTester;
    @Autowired
    private JacksonTester<FavoriteResponse> favoritoResponseTester;
    @Autowired
    private JacksonTester<ReviewRequest> resenaRequestTester;
    @Autowired
    private JacksonTester<ReviewResponse> resenaResponseTester;
    @Autowired
    private JacksonTester<MessageRequest> mensajeRequestTester;
    @Autowired
    private JacksonTester<MessageResponse> mensajeResponseTester;

    // ========== AUTH DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize LoginRequest")
    void testLoginRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        JsonContent<LoginRequest> json = authLoginRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
        assertThat(json).extractingJsonPathStringValue("$.password").isEqualTo("password123");

        LoginRequest deserialized = authLoginRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("should serialize and deserialize RegisterRequest")
    void testRegisterRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        JsonContent<RegisterRequest> json = authRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
        assertThat(json).extractingJsonPathStringValue("$.password").isEqualTo("password123");

        RegisterRequest deserialized = authRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("should serialize and deserialize RegisterResponse")
    void testRegisterResponse() throws Exception {
        RegisterResponse response = new RegisterResponse();
        response.setToken("jwt-token-123");
        response.setRefreshToken("refresh-token-456");
        response.setNombre("Test User");
        response.setUniversidad("Test University");
        response.setFechaRegistro(LocalDateTime.now());

        JsonContent<RegisterResponse> json = authResponseTester.write(response);

        assertThat(json).extractingJsonPathStringValue("$.token").isEqualTo("jwt-token-123");
        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.universidad").isEqualTo("Test University");

        RegisterResponse deserialized = authResponseTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getToken()).isEqualTo("jwt-token-123");
        assertThat(deserialized.getNombre()).isEqualTo("Test User");
    }

    // ========== USER DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize UserUpdateRequest")
    void testUserUpdateRequest() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNombre("Updated Name");
        request.setFotoUrl("https://example.com/photo.jpg");
        request.setUniversidad("Updated University");

        JsonContent<UserUpdateRequest> json = userUpdateRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("Updated Name");
        assertThat(json).extractingJsonPathStringValue("$.fotoUrl").isEqualTo("https://example.com/photo.jpg");
        assertThat(json).extractingJsonPathStringValue("$.universidad").isEqualTo("Updated University");

        UserUpdateRequest deserialized = userUpdateRequestTester.parse(json.getJson()).getObject();
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
    @DisplayName("should serialize and deserialize ProductRequest")
    void testProductRequest() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setTitulo("iPhone 14");
        request.setDescripcion("Smartphone Apple");
        request.setPrecio(1000.0);
        request.setTipo(ProductType.VENTA);
        request.setCategoryId(1L);

        JsonContent<ProductRequest> json = productoRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.titulo").isEqualTo("iPhone 14");
        assertThat(json).extractingJsonPathNumberValue("$.precio").isEqualTo(1000.0);

        ProductRequest deserialized = productoRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getTitulo()).isEqualTo("iPhone 14");
    }

    // ========== CATEGORIA DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize CategoryRequest")
    void testCategoryRequest() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        request.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");

        JsonContent<CategoryRequest> json = categoriaRequestTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");

        CategoryRequest deserialized = categoriaRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
    }

    @Test
    @DisplayName("should serialize and deserialize CategoryResponse")
    void testCategoryResponse() throws Exception {
        CategoryResponse response = new CategoryResponse();
        response.setId(1L);
        response.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        response.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");

        JsonContent<CategoryResponse> json = categoriaResponseTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.nombre").isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
    }

    // ========== TRANSACCION DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize TransactionRequest")
    void testTransactionRequest() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setProductId(1L);
        request.setTipo(TransactionType.COMPRA);

        JsonContent<TransactionRequest> json = transaccionRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.productId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.tipo").isEqualTo("COMPRA");

        TransactionRequest deserialized = transaccionRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getProductId()).isEqualTo(1L);
    }

    // ========== FAVORITO DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize FavoriteRequest")
    void testFavoriteRequest() throws Exception {
        FavoriteRequest request = new FavoriteRequest();
        request.setUserId(1L);
        request.setProductId(2L);

        JsonContent<FavoriteRequest> json = favoritoRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.productId").isEqualTo(2);

        FavoriteRequest deserialized = favoritoRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should serialize and deserialize FavoriteResponse")
    void testFavoriteResponse() throws Exception {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(1L);
        response.setProductId(2L);
        response.setProductTitle("iPhone 14");
        response.setProductPrice(1000.0);
        response.setFechaAgregado(LocalDateTime.now());

        JsonContent<FavoriteResponse> json = favoritoResponseTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.productId").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.productTitle").isEqualTo("iPhone 14");

        FavoriteResponse deserialized = favoritoResponseTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getProductTitle()).isEqualTo("iPhone 14");
    }

    // ========== RESENA DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize ReviewRequest")
    void testReviewRequest() throws Exception {
        ReviewRequest request = new ReviewRequest();
        request.setPuntuacion(5);
        request.setComentario("Excelente producto");
        request.setTransactionId(1L);

        JsonContent<ReviewRequest> json = resenaRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.puntuacion").isEqualTo(5);
        assertThat(json).extractingJsonPathStringValue("$.comentario").isEqualTo("Excelente producto");

        ReviewRequest deserialized = resenaRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getPuntuacion()).isEqualTo(5);
    }

    // ========== MENSAJE DTOs ==========

    @Test
    @DisplayName("should serialize and deserialize MessageRequest")
    void testMessageRequest() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setReceptorId(1L);
        request.setProductId(2L);
        request.setContenido("Hola, ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ disponible?");

        JsonContent<MessageRequest> json = mensajeRequestTester.write(request);

        assertThat(json).extractingJsonPathNumberValue("$.receptorId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.contenido").isEqualTo("Hola, ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ disponible?");

        MessageRequest deserialized = mensajeRequestTester.parse(json.getJson()).getObject();
        assertThat(deserialized.getContenido()).isEqualTo("Hola, ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ disponible?");
    }
}