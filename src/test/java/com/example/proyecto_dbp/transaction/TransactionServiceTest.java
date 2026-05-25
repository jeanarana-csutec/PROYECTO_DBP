package com.example.proyecto_dbp.Transaction;

import com.example.proyecto_dbp.event.TransactionCompletedEvent;
import com.example.proyecto_dbp.exception.ForbiddenException;
import com.example.proyecto_dbp.exception.InvalidOperationException;
import com.example.proyecto_dbp.exception.ProductNotAvailableException;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
import com.example.proyecto_dbp.Product.ProductStatus;
import com.example.proyecto_dbp.Product.Product;
import com.example.proyecto_dbp.Product.ProductRepository;
import com.example.proyecto_dbp.Product.ProductType;
import com.example.proyecto_dbp.security.SecurityUtils;
import com.example.proyecto_dbp.user.Role;
import com.example.proyecto_dbp.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transaccionRepository;

    @Mock
    private ProductRepository productoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransactionService transaccionService;

    private User comprador;
    private User vendedor;
    private Product producto;
    private Transaction transaccion;
    private TransactionRequest requestDTO;
    private TransactionResponse responseDTO;

    @BeforeEach
    void setUp() {
        comprador = new User();
        comprador.setId(1L);
        comprador.setNombre("Juan Perez");
        comprador.setEmail("juan@test.com");
        comprador.setRole(Role.USER);

        vendedor = new User();
        vendedor.setId(2L);
        vendedor.setNombre("Maria Lopez");
        vendedor.setEmail("maria@test.com");
        vendedor.setRole(Role.USER);

        producto = new Product();
        producto.setId(1L);
        producto.setTitulo("iPhone 14");
        producto.setPrecio(1000.0);
        producto.setEstado(ProductStatus.DISPONIBLE);
        producto.setTipo(ProductType.VENTA);
        producto.setVendedor(vendedor);

        transaccion = new Transaction();
        transaccion.setId(1L);
        transaccion.setComprador(comprador);
        transaccion.setVendedor(vendedor);
        transaccion.setProduct(producto);
        transaccion.setTipo(TransactionType.COMPRA);
        transaccion.setEstado(TransactionStatus.PENDIENTE);
        transaccion.setMonto(1000.0);
        transaccion.setFechaInicio(LocalDateTime.now());

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

        setupSecurityUtils();
    }

    private void setupSecurityUtils() {
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
    }

    @Test
    @DisplayName("should create transaccion when valid data")
    void shouldCreateTransactionWhenValidData() throws Exception {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(transaccionRepository.save(any(Transaction.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaction.class), eq(TransactionResponse.class))).thenReturn(responseDTO);

        // When
        TransactionResponse result = transaccionService.crear(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMonto()).isEqualTo(1000.0);
        verify(productoRepository).save(any(Product.class));
        verify(transaccionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("should throw exception when buying own product")
    void shouldThrowExceptionWhenBuyingOwnProduct() {
        // Given
        producto.setVendedor(comprador); // Mismo vendedor que comprador
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When/Then
        assertThatThrownBy(() -> transaccionService.crear(requestDTO))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("No puedes comprar tu propio producto");
    }

    @Test
    @DisplayName("should throw exception when producto not available")
    void shouldThrowExceptionWhenProductNotAvailable() {
        // Given
        producto.setEstado(ProductStatus.VENDIDO);
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When/Then
        assertThatThrownBy(() -> transaccionService.crear(requestDTO))
                .isInstanceOf(ProductNotAvailableException.class)
                .hasMessageContaining("El producto no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ disponible");
    }

    @Test
    @DisplayName("should throw exception when alquiler without fechaFin")
    void shouldThrowExceptionWhenAlquilerWithoutFechaFin() {
        // Given
        requestDTO.setTipo(TransactionType.ALQUILER);
        requestDTO.setFechaFin(null);
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When/Then
        assertThatThrownBy(() -> transaccionService.crear(requestDTO))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("requiere una fecha de fin");
    }

    @Test
    @DisplayName("should complete transaccion when vendedor")
    void shouldCompleteTransactionWhenVendedor() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(vendedor);
        when(transaccionRepository.save(any(Transaction.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaction.class), eq(TransactionResponse.class))).thenReturn(responseDTO);

        // When
        TransactionResponse result = transaccionService.completar(1L);

        // Then
        assertThat(result.getEstado()).isEqualTo(TransactionStatus.PENDIENTE);
        verify(eventPublisher).publishEvent(any(TransactionCompletedEvent.class));
    }

    @Test
    @DisplayName("should throw ForbiddenException when non-vendedor tries to complete")
    void shouldThrowForbiddenExceptionWhenNonVendedorCompletes() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);

        // When/Then
        assertThatThrownBy(() -> transaccionService.completar(1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Solo el vendedor");
    }

    @Test
    @DisplayName("should cancel transaccion when comprador")
    void shouldCancelTransactionWhenComprador() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(transaccionRepository.save(any(Transaction.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaction.class), eq(TransactionResponse.class))).thenReturn(responseDTO);

        // When
        TransactionResponse result = transaccionService.cancelar(1L);

        // Then
        assertThat(result).isNotNull();
        verify(productoRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("should throw exception when canceling non-pending transaccion")
    void shouldThrowExceptionWhenCancelingNonPending() {
        // Given
        transaccion.setEstado(TransactionStatus.COMPLETADA);
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);

        // When/Then
        assertThatThrownBy(() -> transaccionService.cancelar(1L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Solo se pueden cancelar transacciones pendientes");
    }

    @Test
    @DisplayName("should get mis compras")
    void shouldGetMisCompras() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        Page<Transaction> page = new PageImpl<>(List.of(transaccion));
        when(transaccionRepository.findByCompradorId(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Transaction.class), eq(TransactionResponse.class))).thenReturn(responseDTO);

        // When
        Page<TransactionResponse> compras = transaccionService.misCompras(Pageable.unpaged());

        // Then
        assertThat(compras).hasSize(1);
        assertThat(compras.getContent().get(0).getCompradorNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("should get mis ventas")
    void shouldGetMisVentas() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        Page<Transaction> page = new PageImpl<>(List.of(transaccion));
        when(transaccionRepository.findByVendedorId(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Transaction.class), eq(TransactionResponse.class))).thenReturn(responseDTO);

        // When
        Page<TransactionResponse> ventas = transaccionService.misVentas(Pageable.unpaged());

        // Then
        assertThat(ventas).hasSize(1);
    }
}