package com.duoc.inscripcion_cursos.service;

import com.duoc.inscripcion_cursos.dto.request.InscripcionRequestDto;
import com.duoc.inscripcion_cursos.dto.response.InscripcionResponseDto;

public interface InscripcionService {

    InscripcionResponseDto crearInscripcion(InscripcionRequestDto requestDto);
}
