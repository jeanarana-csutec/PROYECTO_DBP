package com.example.proyecto_dbp.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Usa H2 automáticamente, no necesita Docker
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setNombre("Juan Perez");
        user.setEmail("juan@test.com");
        user.setPassword("encodedPassword123");
        user.setRol(Rol.USER);
        user.setUniversidad("Universidad Catolica");
        user.setFotoUrl("https://example.com/foto.jpg");
        user.setFechaRegistro(LocalDateTime.now());
        user.setActivo(true);
    }

    @Test
    @DisplayName("should save user when valid data provided")
    void shouldSaveUserWhenValidData() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("should find user by email when email exists")
    void shouldFindByEmailWhenEmailExists() {
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("juan@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("should return empty when finding by email that does not exist")
    void shouldReturnEmptyWhenFindByEmailNotExists() {
        Optional<User> found = userRepository.findByEmail("noexiste@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should return true when checking existing email")
    void shouldReturnTrueWhenExistsByEmailExists() {
        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmail("juan@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false when checking non-existing email")
    void shouldReturnFalseWhenExistsByEmailNotExists() {
        boolean exists = userRepository.existsByEmail("noexiste@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should update user when modifying fields")
    void shouldUpdateUserWhenModifyingFields() {
        User savedUser = entityManager.persistAndFlush(user);

        savedUser.setNombre("Juan Carlos Perez");
        savedUser.setUniversidad("Nueva Universidad");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        assertThat(updatedUser.getNombre()).isEqualTo("Juan Carlos Perez");
        assertThat(updatedUser.getUniversidad()).isEqualTo("Nueva Universidad");
    }

    @Test
    @DisplayName("should delete user when id exists")
    void shouldDeleteUserWhenIdExists() {
        User savedUser = entityManager.persistAndFlush(user);
        Long id = savedUser.getId();

        userRepository.deleteById(id);
        entityManager.flush();

        Optional<User> found = userRepository.findById(id);
        assertThat(found).isEmpty();
    }
}