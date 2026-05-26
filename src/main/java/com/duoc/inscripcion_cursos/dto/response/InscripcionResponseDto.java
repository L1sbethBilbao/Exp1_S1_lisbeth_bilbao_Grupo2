package com.duoc.inscripcion_cursos.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InscripcionResponseDto(
        Long id,
        String nombreEstudiante,
        String emailEstudiante,
        LocalDateTime fechaInscripcion,
        List<InscripcionDetalleResponseDto> cursos,
        BigDecimal total
) {
}
