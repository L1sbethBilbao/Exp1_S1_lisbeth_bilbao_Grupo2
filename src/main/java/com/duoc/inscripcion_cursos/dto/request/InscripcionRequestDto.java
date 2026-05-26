package com.duoc.inscripcion_cursos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionRequestDto {

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Size(max = 150, message = "El nombre del estudiante no puede superar 150 caracteres")
    private String nombreEstudiante;

    @Email(message = "El email del estudiante no tiene un formato valido")
    @Size(max = 200, message = "El email no puede superar 200 caracteres")
    private String emailEstudiante;

    @NotEmpty(message = "Debe seleccionar al menos un curso")
    @Size(max = 10, message = "No se pueden inscribir mas de 10 cursos en una sola inscripcion")
    private List<Long> cursoIds;
}
