package com.example.proyecto_dbp.Categoria;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con el id: " + id));
        return convertirAResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        // Validamos que no exista otra categoría con el mismo nombre
        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }

        // Mapear RequestDTO -> Entidad
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return convertirAResponseDTO(categoriaGuardada);
    }

    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con el id: " + id));

        // Si cambia el nombre, validar que el nuevo nombre no esté duplicado por otra categoría
        if (!categoriaExistente.getNombre().equals(dto.getNombre()) &&
                categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe otra categoría con el nombre: " + dto.getNombre());
        }

        // Actualizamos los datos
        categoriaExistente.setNombre(dto.getNombre());
        categoriaExistente.setDescripcion(dto.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);
        return convertirAResponseDTO(categoriaActualizada);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    // --- MÉTODO AUXILIAR DE MAPEO (Entity -> ResponseDTO) ---
    private CategoriaResponseDTO convertirAResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }
}