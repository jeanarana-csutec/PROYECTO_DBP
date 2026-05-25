// CategoryService.java
package com.example.proyecto_dbp.Category;

import com.example.proyecto_dbp.exception.DuplicateResourceException;
import com.example.proyecto_dbp.exception.ResourceNotFoundException;
import com.example.proyecto_dbp.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoriaRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse obtenerPorId(Long id) {
        Category categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: " + id));
        return modelMapper.map(categoria, CategoryResponse.class);
    }

    @Transactional
    public CategoryResponse crear(CategoryRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Ya existe una categorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a con ese nombre");
        }
        Category categoria = modelMapper.map(request, Category.class);
        return modelMapper.map(categoriaRepository.save(categoria), CategoryResponse.class);
    }

    @Transactional
    public CategoryResponse actualizar(Long id, CategoryRequest request) {
        Category categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: " + id));
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return modelMapper.map(categoriaRepository.save(categoria), CategoryResponse.class);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}