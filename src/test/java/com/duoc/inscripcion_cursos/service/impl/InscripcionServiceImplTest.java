package com.duoc.inscripcion_cursos.service.impl;

import com.duoc.inscripcion_cursos.dto.request.InscripcionRequestDto;
import com.duoc.inscripcion_cursos.entity.Curso;
import com.duoc.inscripcion_cursos.exception.BusinessException;
import com.duoc.inscripcion_cursos.exception.ResourceNotFoundException;
import com.duoc.inscripcion_cursos.mapper.InscripcionMapper;
import com.duoc.inscripcion_cursos.repository.CursoRepository;
import com.duoc.inscripcion_cursos.repository.InscripcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceImplTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private InscripcionMapper inscripcionMapper;

    @InjectMocks
    private InscripcionServiceImpl inscripcionService;

    @Test
    void calcularTotal_sumaCostosDeCursos() {
        List<Curso> cursos = List.of(
                Curso.builder().costo(new BigDecimal("150000")).build(),
                Curso.builder().costo(new BigDecimal("250000")).build()
        );

        BigDecimal total = InscripcionServiceImpl.calcularTotal(cursos);

        assertThat(total).isEqualByComparingTo("400000");
    }

    @Test
    void crearInscripcion_lanzaErrorSiCursoNoExiste() {
        InscripcionRequestDto request = InscripcionRequestDto.builder()
                .nombreEstudiante("Maria Lopez")
                .cursoIds(List.of(99L))
                .build();

        when(cursoRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThatThrownBy(() -> inscripcionService.crearInscripcion(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crearInscripcion_lanzaErrorSiHayCursosDuplicados() {
        InscripcionRequestDto request = InscripcionRequestDto.builder()
                .nombreEstudiante("Maria Lopez")
                .cursoIds(List.of(1L, 1L))
                .build();

        assertThatThrownBy(() -> inscripcionService.crearInscripcion(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("duplicados");
    }
}
