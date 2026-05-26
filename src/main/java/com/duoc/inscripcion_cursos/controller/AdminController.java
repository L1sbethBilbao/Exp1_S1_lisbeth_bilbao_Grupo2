package com.duoc.inscripcion_cursos.controller;

import com.duoc.inscripcion_cursos.service.DatosPruebaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones de administracion (requiere rol ADMIN)")
public class AdminController {

    private final DatosPruebaService datosPruebaService;

    @PostMapping("/reset-pruebas")
    @Operation(
            summary = "Resetear datos de prueba",
            description = "Elimina inscripciones y cursos, reinicia secuencias y carga los 3 cursos iniciales. Solo rol ADMIN."
    )
    public ResponseEntity<Map<String, String>> resetearDatosPrueba() {
        datosPruebaService.resetearDatosPrueba();
        return ResponseEntity.ok(Map.of(
                "message", "Datos de prueba reseteados. Puedes ejecutar Postman 01-07 desde cero."
        ));
    }
}
