package com.duoc.inscripcion_cursos.service.impl;

import com.duoc.inscripcion_cursos.dto.request.InscripcionRequestDto;
import com.duoc.inscripcion_cursos.dto.response.InscripcionResponseDto;
import com.duoc.inscripcion_cursos.entity.Curso;
import com.duoc.inscripcion_cursos.entity.Inscripcion;
import com.duoc.inscripcion_cursos.entity.InscripcionDetalle;
import com.duoc.inscripcion_cursos.exception.BusinessException;
import com.duoc.inscripcion_cursos.exception.ResourceNotFoundException;
import com.duoc.inscripcion_cursos.mapper.InscripcionMapper;
import com.duoc.inscripcion_cursos.repository.CursoRepository;
import com.duoc.inscripcion_cursos.repository.InscripcionRepository;
import com.duoc.inscripcion_cursos.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InscripcionServiceImpl implements InscripcionService {

    private static final Logger log = LoggerFactory.getLogger(InscripcionServiceImpl.class);

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionMapper inscripcionMapper;

    @Override
    @Transactional
    public InscripcionResponseDto crearInscripcion(InscripcionRequestDto requestDto) {
        List<Long> cursoIds = requestDto.getCursoIds();
        validarCursosDuplicados(cursoIds);

        List<Curso> cursos = cursoRepository.findAllById(cursoIds);
        validarCursosExistentes(cursoIds, cursos);

        BigDecimal total = calcularTotal(cursos);

        Inscripcion inscripcion = Inscripcion.builder()
                .nombreEstudiante(requestDto.getNombreEstudiante().trim())
                .emailEstudiante(normalizarEmail(requestDto.getEmailEstudiante()))
                .fechaInscripcion(LocalDateTime.now())
                .total(total)
                .build();

        for (Curso curso : cursos) {
            InscripcionDetalle detalle = InscripcionDetalle.builder()
                    .curso(curso)
                    .costoUnitario(curso.getCosto())
                    .build();
            inscripcion.agregarDetalle(detalle);
        }

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        log.info("Inscripcion creada id={} para estudiante={} con total={}",
                guardada.getId(), guardada.getNombreEstudiante(), guardada.getTotal());

        return inscripcionMapper.toResponse(guardada);
    }

    static BigDecimal calcularTotal(List<Curso> cursos) {
        return cursos.stream()
                .map(Curso::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarCursosDuplicados(List<Long> cursoIds) {
        Set<Long> unicos = new HashSet<>(cursoIds);
        if (unicos.size() != cursoIds.size()) {
            throw new BusinessException("La inscripcion no puede incluir cursos duplicados");
        }
    }

    private void validarCursosExistentes(List<Long> solicitados, List<Curso> encontrados) {
        if (encontrados.size() != solicitados.size()) {
            Set<Long> encontradosIds = new HashSet<>();
            encontrados.forEach(curso -> encontradosIds.add(curso.getId()));

            Long faltante = solicitados.stream()
                    .filter(id -> !encontradosIds.contains(id))
                    .findFirst()
                    .orElseThrow();

            throw new ResourceNotFoundException("No existe el curso con id: " + faltante);
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }
}
