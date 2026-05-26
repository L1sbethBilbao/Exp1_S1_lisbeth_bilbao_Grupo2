package com.duoc.inscripcion_cursos.service.impl;

import com.duoc.inscripcion_cursos.dto.request.CursoRequestDto;
import com.duoc.inscripcion_cursos.dto.response.CursoResponseDto;
import com.duoc.inscripcion_cursos.entity.Curso;
import com.duoc.inscripcion_cursos.exception.DuplicateResourceException;
import com.duoc.inscripcion_cursos.mapper.CursoMapper;
import com.duoc.inscripcion_cursos.repository.CursoRepository;
import com.duoc.inscripcion_cursos.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoServiceImpl implements CursoService {

    private static final Logger log = LoggerFactory.getLogger(CursoServiceImpl.class);

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;

    @Override
    public List<CursoResponseDto> listarCursos() {
        return cursoRepository.findAll().stream()
                .sorted(Comparator.comparing(Curso::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(cursoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CursoResponseDto crearCurso(CursoRequestDto requestDto) {
        String nombreNormalizado = requestDto.getNombre().trim();

        if (cursoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new DuplicateResourceException("Ya existe un curso con el nombre: " + nombreNormalizado);
        }

        Curso curso = cursoMapper.toEntity(requestDto);
        curso.setNombre(nombreNormalizado);
        curso.setInstructor(requestDto.getInstructor().trim());

        Curso guardado = cursoRepository.save(curso);
        log.info("Curso creado con id={} y nombre={}", guardado.getId(), guardado.getNombre());

        return cursoMapper.toResponse(guardado);
    }
}
