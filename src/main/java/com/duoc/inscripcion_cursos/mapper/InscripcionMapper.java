package com.duoc.inscripcion_cursos.mapper;

import com.duoc.inscripcion_cursos.dto.response.InscripcionDetalleResponseDto;
import com.duoc.inscripcion_cursos.dto.response.InscripcionResponseDto;
import com.duoc.inscripcion_cursos.entity.Inscripcion;
import com.duoc.inscripcion_cursos.entity.InscripcionDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InscripcionMapper {

    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "nombreCurso", source = "curso.nombre")
    @Mapping(target = "instructor", source = "curso.instructor")
    @Mapping(target = "duracionHoras", source = "curso.duracionHoras")
    InscripcionDetalleResponseDto toDetalleResponse(InscripcionDetalle detalle);

    List<InscripcionDetalleResponseDto> toDetalleResponseList(List<InscripcionDetalle> detalles);

    @Mapping(target = "cursos", source = "detalles")
    InscripcionResponseDto toResponse(Inscripcion inscripcion);
}
