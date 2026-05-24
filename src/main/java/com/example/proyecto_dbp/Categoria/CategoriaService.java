// CategoriaService.java
package com.example.proyecto_dbp.Categoria;

import com.example.proyecto_dbp.Exceptions.DuplicateResource;
import com.example.proyecto_dbp.Exceptions.ResourceNotFound;
import com.example.proyecto_dbp.Exceptions.UserAlreadyExists;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CategoriaResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Categoría no encontrada con id: " + id));
        return modelMapper.map(categoria, CategoriaResponseDTO.class);
    }

    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResource("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = modelMapper.map(request, Categoria.class);
        return modelMapper.map(categoriaRepository.save(categoria), CategoriaResponseDTO.class);
    }

    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Categoría no encontrada con id: " + id));
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return modelMapper.map(categoriaRepository.save(categoria), CategoriaResponseDTO.class);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFound("Categoría no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}