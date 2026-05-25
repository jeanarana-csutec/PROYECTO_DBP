package com.example.proyecto_dbp.Categoria;


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
@DisplayName("Categoria Repository Tests")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos y tecnología");
    }

    @Test
    @DisplayName("should save categoria when valid data provided")
    void shouldSaveCategoriaWhenValidData() {
        // When
        Categoria saved = categoriaRepository.save(categoria);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Electrónica");
        assertThat(saved.getDescripcion()).isEqualTo("Productos electrónicos y tecnología");
    }

    @Test
    @DisplayName("should find categoria by id when exists")
    void shouldFindByIdWhenExists() {
        // Given
        Categoria saved = entityManager.persistAndFlush(categoria);

        // When
        Optional<Categoria> found = categoriaRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Electrónica");
    }

    @Test
    @DisplayName("should return empty when finding by non-existing id")
    void shouldReturnEmptyWhenFindByNonExistingId() {
        // When
        Optional<Categoria> found = categoriaRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find categoria by nombre when exists")
    void shouldFindByNombreWhenExists() {
        // Given
        entityManager.persistAndFlush(categoria);

        // When
        Optional<Categoria> found = categoriaRepository.findByNombre("Electrónica");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Electrónica");
    }

    @Test
    @DisplayName("should return empty when finding by non-existing nombre")
    void shouldReturnEmptyWhenFindByNonExistingNombre() {
        // When
        Optional<Categoria> found = categoriaRepository.findByNombre("Inexistente");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should return true when existsByNombre for existing nombre")
    void shouldReturnTrueWhenExistsByNombreExists() {
        // Given
        entityManager.persistAndFlush(categoria);

        // When
        boolean exists = categoriaRepository.existsByNombre("Electrónica");

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
    void shouldUpdateCategoriaWhenModifyingFields() {
        // Given
        Categoria saved = entityManager.persistAndFlush(categoria);

        // When
        saved.setNombre("Computación");
        saved.setDescripcion("Equipos de cómputo");
        Categoria updated = categoriaRepository.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getNombre()).isEqualTo("Computación");
        assertThat(updated.getDescripcion()).isEqualTo("Equipos de cómputo");
    }

    @Test
    @DisplayName("should delete categoria when id exists")
    void shouldDeleteCategoriaWhenIdExists() {
        // Given
        Categoria saved = entityManager.persistAndFlush(categoria);
        Long id = saved.getId();

        // When
        categoriaRepository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<Categoria> found = categoriaRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should return all categorias when findAll")
    void shouldReturnAllCategoriasWhenFindAll() {
        // Given
        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Hogar");
        categoria2.setDescripcion("Productos para el hogar");

        entityManager.persistAndFlush(categoria);
        entityManager.persistAndFlush(categoria2);

        // When
        List<Categoria> categorias = categoriaRepository.findAll();

        // Then
        assertThat(categorias).hasSize(2);
        assertThat(categorias).extracting(Categoria::getNombre)
                .containsExactlyInAnyOrder("Electrónica", "Hogar");
    }

    @Test
    @DisplayName("should throw exception when saving categoria with duplicate nombre")
    void shouldThrowExceptionWhenSavingDuplicateNombre() {
        // Given
        entityManager.persistAndFlush(categoria);
        Categoria duplicate = new Categoria();
        duplicate.setNombre("Electrónica"); // Mismo nombre
        duplicate.setDescripcion("Otra descripción");

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            categoriaRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("should handle categoria with null descripcion")
    void shouldHandleCategoriaWithNullDescripcion() {
        // Given
        categoria.setDescripcion(null);

        // When
        Categoria saved = categoriaRepository.save(categoria);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescripcion()).isNull();
    }
}