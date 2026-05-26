package com.duoc.inscripcion_cursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoRequestDto {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 150, message = "El nombre del curso no puede superar 150 caracteres")
    private String nombre;

    @NotBlank(message = "El instructor es obligatorio")
    @Size(max = 150, message = "El instructor no puede superar 150 caracteres")
    private String instructor;

    @NotNull(message = "La duracion en horas es obligatoria")
    @Positive(message = "La duracion debe ser mayor a cero")
    private Integer duracionHoras;

    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser mayor a cero")
    private BigDecimal costo;
}
