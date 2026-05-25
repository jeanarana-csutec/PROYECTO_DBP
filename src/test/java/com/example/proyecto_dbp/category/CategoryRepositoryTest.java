package com.example.proyecto_dbp.Category;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Category Repository Tests")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category categoria;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        categoria.setDescripcion("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos y tecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");
    }

    @Test
    @DisplayName("should save categoria when valid data provided")
    void shouldSaveCategoryWhenValidData() {
        // When
        Category saved = categoriaRepository.save(categoria);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
        assertThat(saved.getDescripcion()).isEqualTo("Products electrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nicos y tecnologÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a");
    }

    @Test
    @DisplayName("should find categoria by id when exists")
    void shouldFindByIdWhenExists() {
        // Given
        Category saved = entityManager.persistAndFlush(categoria);

        // When
        Optional<Category> found = categoriaRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
    }

    @Test
    @DisplayName("should return empty when finding by non-existing id")
    void shouldReturnEmptyWhenFindByNonExistingId() {
        // When
        Optional<Category> found = categoriaRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find categoria by nombre when exists")
    void shouldFindByNombreWhenExists() {
        // Given
        entityManager.persistAndFlush(categoria);

        // When
        Optional<Category> found = categoriaRepository.findByNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");
    }

    @Test
    @DisplayName("should return empty when finding by non-existing nombre")
    void shouldReturnEmptyWhenFindByNonExistingNombre() {
        // When
        Optional<Category> found = categoriaRepository.findByNombre("Inexistente");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should return true when existsByNombre for existing nombre")
    void shouldReturnTrueWhenExistsByNombreExists() {
        // Given
        entityManager.persistAndFlush(categoria);

        // When
        boolean exists = categoriaRepository.existsByNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false when existsByNombre for non-existing nombre")
    void shouldReturnFalseWhenExistsByNombreNotExists() {
        // When
        boolean exists = categoriaRepository.existsByNombre("Inexistente");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should update categoria when modifying fields")
    void shouldUpdateCategoryWhenModifyingFields() {
        // Given
        Category saved = entityManager.persistAndFlush(categoria);

        // When
        saved.setNombre("ComputaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        saved.setDescripcion("Equipos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³mputo");
        Category updated = categoriaRepository.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getNombre()).isEqualTo("ComputaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
        assertThat(updated.getDescripcion()).isEqualTo("Equipos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³mputo");
    }

    @Test
    @DisplayName("should delete categoria when id exists")
    void shouldDeleteCategoryWhenIdExists() {
        // Given
        Category saved = entityManager.persistAndFlush(categoria);
        Long id = saved.getId();

        // When
        categoriaRepository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<Category> found = categoriaRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should return all categorias when findAll")
    void shouldReturnAllCategorysWhenFindAll() {
        // Given
        Category categoria2 = new Category();
        categoria2.setNombre("Hogar");
        categoria2.setDescripcion("Products para el hogar");

        entityManager.persistAndFlush(categoria);
        entityManager.persistAndFlush(categoria2);

        // When
        List<Category> categorias = categoriaRepository.findAll();

        // Then
        assertThat(categorias).hasSize(2);
        assertThat(categorias).extracting(Category::getNombre)
                .containsExactlyInAnyOrder("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica", "Hogar");
    }

    @Test
    @DisplayName("should throw exception when saving categoria with duplicate nombre")
    void shouldThrowExceptionWhenSavingDuplicateNombre() {
        // Given
        entityManager.persistAndFlush(categoria);
        Category duplicate = new Category();
        duplicate.setNombre("ElectrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³nica"); // Mismo nombre
        duplicate.setDescripcion("Otra descripciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            categoriaRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("should handle categoria with null descripcion")
    void shouldHandleCategoryWithNullDescripcion() {
        // Given
        categoria.setDescripcion(null);

        // When
        Category saved = categoriaRepository.save(categoria);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescripcion()).isNull();
    }
}