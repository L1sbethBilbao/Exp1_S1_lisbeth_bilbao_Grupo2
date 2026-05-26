package com.duoc.inscripcion_cursos.controller;

import com.duoc.inscripcion_cursos.dto.request.InscripcionRequestDto;
import com.duoc.inscripcion_cursos.dto.response.InscripcionResponseDto;
import com.duoc.inscripcion_cursos.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Inscripcion de estudiantes en uno o mas cursos")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping
    @Operation(summary = "Crear inscripcion", description = "Inscribe un estudiante en uno o mas cursos y devuelve el resumen con el total")
    public ResponseEntity<InscripcionResponseDto> crearInscripcion(@Valid @RequestBody InscripcionRequestDto requestDto) {
        InscripcionResponseDto response = inscripcionService.crearInscripcion(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
