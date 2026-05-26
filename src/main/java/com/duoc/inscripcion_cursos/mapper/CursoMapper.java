package com.duoc.inscripcion_cursos.mapper;

import com.duoc.inscripcion_cursos.dto.request.CursoRequestDto;
import com.duoc.inscripcion_cursos.dto.response.CursoResponseDto;
import com.duoc.inscripcion_cursos.entity.Curso;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    CursoResponseDto toResponse(Curso curso);

    List<CursoResponseDto> toResponseList(List<Curso> cursos);

    Curso toEntity(CursoRequestDto requestDto);
}
