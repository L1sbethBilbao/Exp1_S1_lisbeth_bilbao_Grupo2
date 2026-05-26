package com.duoc.inscripcion_cursos.controller;

import com.duoc.inscripcion_cursos.dto.request.CursoRequestDto;
import com.duoc.inscripcion_cursos.dto.response.CursoResponseDto;
import com.duoc.inscripcion_cursos.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Consulta y registro de cursos disponibles")
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    @Operation(summary = "Listar cursos", description = "Obtiene la lista de cursos con nombre, instructor, duracion y costo")
    public ResponseEntity<List<CursoResponseDto>> listarCursos() {
        return ResponseEntity.ok(cursoService.listarCursos());
    }

    @PostMapping
    @Operation(summary = "Agregar curso", description = "Registra un nuevo curso en Oracle Cloud")
    public ResponseEntity<CursoResponseDto> crearCurso(@Valid @RequestBody CursoRequestDto requestDto) {
        CursoResponseDto response = cursoService.crearCurso(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
