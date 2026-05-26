package com.duoc.inscripcion_cursos.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InscripcionDetalleResponseDto(
        Long cursoId,
        String nombreCurso,
        String instructor,
        Integer duracionHoras,
        BigDecimal costoUnitario
) {
}
