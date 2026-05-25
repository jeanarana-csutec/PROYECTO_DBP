package com.example.proyecto_dbp.Categoria;

import com.example.proyecto_dbp.Exceptions.DuplicateResource;
import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Categoria Service Tests")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO requestDTO;
    private CategoriaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Electrónica");
        requestDTO.setDescripcion("Productos electrónicos");

        responseDTO = new CategoriaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre("Electrónica");
        responseDTO.setDescripcion("Productos electrónicos");
    }

    @Test
    @DisplayName("should return all categorias when obtenerTodas")
    void shouldReturnAllCategoriasWhenObtenerTodas() {
        // Given
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre("Hogar");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria, categoria2));
        when(modelMapper.map(categoria, CategoriaResponseDTO.class)).thenReturn(responseDTO);

        CategoriaResponseDTO responseDTO2 = new CategoriaResponseDTO();
        responseDTO2.setId(2L);
        responseDTO2.setNombre("Hogar");
        when(modelMapper.map(categoria2, CategoriaResponseDTO.class)).thenReturn(responseDTO2);

        // When
        List<CategoriaResponseDTO> result = categoriaService.obtenerTodas();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Electrónica");
        assertThat(result.get(1).getNombre()).isEqualTo("Hogar");
        verify(categoriaRepository).findAll();
    }

    @Test
    @DisplayName("should return categoria by id when exists")
    void shouldReturnCategoriaByIdWhenExists() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(modelMapper.map(categoria, CategoriaResponseDTO.class)).thenReturn(responseDTO);

        // When
        CategoriaResponseDTO result = categoriaService.obtenerPorId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Electrónica");
        verify(categoriaRepository).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFound when categoria not found by id")
    void shouldThrowResourceNotFoundWhenCategoriaNotFound() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoriaService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Categoría no encontrada con id: 999");
    }

    @Test
    @DisplayName("should create categoria when valid data")
    void shouldCreateCategoriaWhenValidData() {
        // Given
        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(false);
        when(modelMapper.map(requestDTO, Categoria.class)).thenReturn(categoria);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(modelMapper.map(categoria, CategoriaResponseDTO.class)).thenReturn(responseDTO);

        // When
        CategoriaResponseDTO result = categoriaService.crear(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Electrónica");
        verify(categoriaRepository).existsByNombre("Electrónica");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("should throw DuplicateResource when creating categoria with existing nombre")
    void shouldThrowDuplicateResourceWhenNombreAlreadyExists() {
        // Given
        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoriaService.crear(requestDTO))
                .isInstanceOf(DuplicateResource.class)
                .hasMessageContaining("Ya existe una categoría con ese nombre");

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("should update categoria when valid data")
    void shouldUpdateCategoriaWhenValidData() {
        // Given
        CategoriaRequestDTO updateRequest = new CategoriaRequestDTO();
        updateRequest.setNombre("Computación");
        updateRequest.setDescripcion("Equipos de cómputo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO updatedResponse = new CategoriaResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setNombre("Computación");
        updatedResponse.setDescripcion("Equipos de cómputo");
        when(modelMapper.map(categoria, CategoriaResponseDTO.class)).thenReturn(updatedResponse);

        // When
        CategoriaResponseDTO result = categoriaService.actualizar(1L, updateRequest);

        // Then
        assertThat(result.getNombre()).isEqualTo("Computación");
        assertThat(result.getDescripcion()).isEqualTo("Equipos de cómputo");
        verify(categoriaRepository).save(categoria);
    }

    @Test
    @DisplayName("should throw ResourceNotFound when updating non-existing categoria")
    void shouldThrowResourceNotFoundWhenUpdatingNonExisting() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoriaService.actualizar(999L, requestDTO))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Categoría no encontrada con id: 999");
    }

    @Test
    @DisplayName("should delete categoria when exists")
    void shouldDeleteCategoriaWhenExists() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        // When
        categoriaService.eliminar(1L);

        // Then
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFound when deleting non-existing categoria")
    void shouldThrowResourceNotFoundWhenDeletingNonExisting() {
        // Given
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> categoriaService.eliminar(999L))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Categoría no encontrada con id: 999");

        verify(categoriaRepository, never()).deleteById(anyLong());
    }
}