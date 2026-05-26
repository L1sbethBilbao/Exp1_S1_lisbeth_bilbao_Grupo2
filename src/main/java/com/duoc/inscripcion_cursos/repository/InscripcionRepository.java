package com.duoc.inscripcion_cursos.repository;

import com.duoc.inscripcion_cursos.entity.Inscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    @EntityGraph(attributePaths = {"detalles", "detalles.curso"})
    Optional<Inscripcion> findWithDetallesById(Long id);
}
