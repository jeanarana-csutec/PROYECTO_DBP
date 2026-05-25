package com.example.proyecto_dbp.Category;

import com.example.proyecto_dbp.exception.DuplicateResourceException;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
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
@DisplayName("Category Service Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoriaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoriaService;

    private Category categoria;
    private CategoryRequest requestDTO;
    private CategoryResponse responseDTO;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setId(1L);
        categoria.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        categoria.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");

        requestDTO = new CategoryRequest();
        requestDTO.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        requestDTO.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");

        responseDTO = new CategoryResponse();
        responseDTO.setId(1L);
        responseDTO.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        responseDTO.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos");
    }

    @Test
    @DisplayName("should return all categorias when obtenerTodas")
    void shouldReturnAllCategorysWhenObtenerTodas() {
        // Given
        Category categoria2 = new Category();
        categoria2.setId(2L);
        categoria2.setNombre("Hogar");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria, categoria2));
        when(modelMapper.map(categoria, CategoryResponse.class)).thenReturn(responseDTO);

        CategoryResponse responseDTO2 = new CategoryResponse();
        responseDTO2.setId(2L);
        responseDTO2.setNombre("Hogar");
        when(modelMapper.map(categoria2, CategoryResponse.class)).thenReturn(responseDTO2);

        // When
        List<CategoryResponse> result = categoriaService.obtenerTodas();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        assertThat(result.get(1).getNombre()).isEqualTo("Hogar");
        verify(categoriaRepository).findAll();
    }

    @Test
    @DisplayName("should return categoria by id when exists")
    void shouldReturnCategoryByIdWhenExists() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(modelMapper.map(categoria, CategoryResponse.class)).thenReturn(responseDTO);

        // When
        CategoryResponse result = categoriaService.obtenerPorId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        verify(categoriaRepository).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when categoria not found by id")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoriaService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: 999");
    }

    @Test
    @DisplayName("should create categoria when valid data")
    void shouldCreateCategoryWhenValidData() {
        // Given
        when(categoriaRepository.existsByNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica")).thenReturn(false);
        when(modelMapper.map(requestDTO, Category.class)).thenReturn(categoria);
        when(categoriaRepository.save(any(Category.class))).thenReturn(categoria);
        when(modelMapper.map(categoria, CategoryResponse.class)).thenReturn(responseDTO);

        // When
        CategoryResponse result = categoriaService.crear(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        verify(categoriaRepository).existsByNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        verify(categoriaRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when creating categoria with existing nombre")
    void shouldThrowDuplicateResourceExceptionWhenNombreAlreadyExists() {
        // Given
        when(categoriaRepository.existsByNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoriaService.crear(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ya existe una categorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a con ese nombre");

        verify(categoriaRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("should update categoria when valid data")
    void shouldUpdateCategoryWhenValidData() {
        // Given
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setNombre("ComputaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        updateRequest.setDescripcion("Equipos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³mputo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Category.class))).thenReturn(categoria);

        CategoryResponse updatedResponse = new CategoryResponse();
        updatedResponse.setId(1L);
        updatedResponse.setNombre("ComputaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        updatedResponse.setDescripcion("Equipos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³mputo");
        when(modelMapper.map(categoria, CategoryResponse.class)).thenReturn(updatedResponse);

        // When
        CategoryResponse result = categoriaService.actualizar(1L, updateRequest);

        // Then
        assertThat(result.getNombre()).isEqualTo("ComputaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        assertThat(result.getDescripcion()).isEqualTo("Equipos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³mputo");
        verify(categoriaRepository).save(categoria);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when updating non-existing categoria")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExisting() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoriaService.actualizar(999L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: 999");
    }

    @Test
    @DisplayName("should delete categoria when exists")
    void shouldDeleteCategoryWhenExists() {
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
    @DisplayName("should throw ResourceNotFoundException when deleting non-existing categoria")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExisting() {
        // Given
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> categoriaService.eliminar(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: 999");

        verify(categoriaRepository, never()).deleteById(anyLong());
    }
}