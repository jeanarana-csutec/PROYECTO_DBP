package com.example.proyecto_dbp.Transaccion;

import com.example.proyecto_dbp.Events.TransaccionCompletadaEvent;
import com.example.proyecto_dbp.Exceptions.Forbidden;
import com.example.proyecto_dbp.Exceptions.InvalidOperation;
import com.example.proyecto_dbp.Exceptions.ProductoNoDisponible;
import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import com.example.proyecto_dbp.Producto.EstadoProducto;
import com.example.proyecto_dbp.Producto.Producto;
import com.example.proyecto_dbp.Producto.ProductoRepository;
import com.example.proyecto_dbp.Producto.TipoProducto;
import com.example.proyecto_dbp.Security.SecurityUtils;
import com.example.proyecto_dbp.User.Rol;
import com.example.proyecto_dbp.User.User;
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
@DisplayName("Transaccion Service Tests")
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransaccionService transaccionService;

    private User comprador;
    private User vendedor;
    private Producto producto;
    private Transaccion transaccion;
    private TransaccionRequestDTO requestDTO;
    private TransaccionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        comprador = new User();
        comprador.setId(1L);
        comprador.setNombre("Juan Perez");
        comprador.setEmail("juan@test.com");
        comprador.setRol(Rol.USER);

        vendedor = new User();
        vendedor.setId(2L);
        vendedor.setNombre("Maria Lopez");
        vendedor.setEmail("maria@test.com");
        vendedor.setRol(Rol.USER);

        producto = new Producto();
        producto.setId(1L);
        producto.setTitulo("iPhone 14");
        producto.setPrecio(1000.0);
        producto.setEstado(EstadoProducto.DISPONIBLE);
        producto.setTipo(TipoProducto.VENTA);
        producto.setVendedor(vendedor);

        transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setComprador(comprador);
        transaccion.setVendedor(vendedor);
        transaccion.setProducto(producto);
        transaccion.setTipo(TipoTransaccion.COMPRA);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
        transaccion.setMonto(1000.0);
        transaccion.setFechaInicio(LocalDateTime.now());

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

        setupSecurityUtils();
    }

    private void setupSecurityUtils() {
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
    }

    @Test
    @DisplayName("should create transaccion when valid data")
    void shouldCreateTransaccionWhenValidData() throws Exception {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponseDTO.class))).thenReturn(responseDTO);

        // When
        TransaccionResponseDTO result = transaccionService.crear(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMonto()).isEqualTo(1000.0);
        verify(productoRepository).save(any(Producto.class));
        verify(transaccionRepository).save(any(Transaccion.class));
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
                .isInstanceOf(InvalidOperation.class)
                .hasMessageContaining("No puedes comprar tu propio producto");
    }

    @Test
    @DisplayName("should throw exception when producto not available")
    void shouldThrowExceptionWhenProductoNotAvailable() {
        // Given
        producto.setEstado(EstadoProducto.VENDIDO);
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When/Then
        assertThatThrownBy(() -> transaccionService.crear(requestDTO))
                .isInstanceOf(ProductoNoDisponible.class)
                .hasMessageContaining("El producto no está disponible");
    }

    @Test
    @DisplayName("should throw exception when alquiler without fechaFin")
    void shouldThrowExceptionWhenAlquilerWithoutFechaFin() {
        // Given
        requestDTO.setTipo(TipoTransaccion.ALQUILER);
        requestDTO.setFechaFin(null);
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When/Then
        assertThatThrownBy(() -> transaccionService.crear(requestDTO))
                .isInstanceOf(InvalidOperation.class)
                .hasMessageContaining("requiere una fecha de fin");
    }

    @Test
    @DisplayName("should complete transaccion when vendedor")
    void shouldCompleteTransaccionWhenVendedor() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(vendedor);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponseDTO.class))).thenReturn(responseDTO);

        // When
        TransaccionResponseDTO result = transaccionService.completar(1L);

        // Then
        assertThat(result.getEstado()).isEqualTo(EstadoTransaccion.PENDIENTE);
        verify(eventPublisher).publishEvent(any(TransaccionCompletadaEvent.class));
    }

    @Test
    @DisplayName("should throw Forbidden when non-vendedor tries to complete")
    void shouldThrowForbiddenWhenNonVendedorCompletes() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);

        // When/Then
        assertThatThrownBy(() -> transaccionService.completar(1L))
                .isInstanceOf(Forbidden.class)
                .hasMessageContaining("Solo el vendedor");
    }

    @Test
    @DisplayName("should cancel transaccion when comprador")
    void shouldCancelTransaccionWhenComprador() {
        // Given
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponseDTO.class))).thenReturn(responseDTO);

        // When
        TransaccionResponseDTO result = transaccionService.cancelar(1L);

        // Then
        assertThat(result).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("should throw exception when canceling non-pending transaccion")
    void shouldThrowExceptionWhenCancelingNonPending() {
        // Given
        transaccion.setEstado(EstadoTransaccion.COMPLETADA);
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);

        // When/Then
        assertThatThrownBy(() -> transaccionService.cancelar(1L))
                .isInstanceOf(InvalidOperation.class)
                .hasMessageContaining("Solo se pueden cancelar transacciones pendientes");
    }

    @Test
    @DisplayName("should get mis compras")
    void shouldGetMisCompras() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        Page<Transaccion> page = new PageImpl<>(List.of(transaccion));
        when(transaccionRepository.findByCompradorId(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponseDTO.class))).thenReturn(responseDTO);

        // When
        Page<TransaccionResponseDTO> compras = transaccionService.misCompras(Pageable.unpaged());

        // Then
        assertThat(compras).hasSize(1);
        assertThat(compras.getContent().get(0).getCompradorNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("should get mis ventas")
    void shouldGetMisVentas() {
        // Given
        when(securityUtils.getUsuarioAutenticado()).thenReturn(comprador);
        Page<Transaccion> page = new PageImpl<>(List.of(transaccion));
        when(transaccionRepository.findByVendedorId(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponseDTO.class))).thenReturn(responseDTO);

        // When
        Page<TransaccionResponseDTO> ventas = transaccionService.misVentas(Pageable.unpaged());

        // Then
        assertThat(ventas).hasSize(1);
    }
}