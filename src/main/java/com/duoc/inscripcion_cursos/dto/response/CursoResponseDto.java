package com.duoc.inscripcion_cursos.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CursoResponseDto(
        Long id,
        String nombre,
        String instructor,
        Integer duracionHoras,
        BigDecimal costo
) {
}
