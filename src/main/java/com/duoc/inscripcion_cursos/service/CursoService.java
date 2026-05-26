package com.duoc.inscripcion_cursos.service;

import com.duoc.inscripcion_cursos.dto.request.CursoRequestDto;
import com.duoc.inscripcion_cursos.dto.response.CursoResponseDto;

import java.util.List;

public interface CursoService {

    List<CursoResponseDto> listarCursos();

    CursoResponseDto crearCurso(CursoRequestDto requestDto);
}
