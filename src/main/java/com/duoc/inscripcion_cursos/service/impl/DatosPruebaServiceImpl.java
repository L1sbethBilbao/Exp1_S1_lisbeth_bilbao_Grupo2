package com.duoc.inscripcion_cursos.service.impl;

import com.duoc.inscripcion_cursos.entity.Curso;
import com.duoc.inscripcion_cursos.repository.CursoRepository;
import com.duoc.inscripcion_cursos.repository.InscripcionRepository;
import com.duoc.inscripcion_cursos.service.DatosPruebaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatosPruebaServiceImpl implements DatosPruebaService {

    private static final Logger log = LoggerFactory.getLogger(DatosPruebaServiceImpl.class);

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void resetearDatosPrueba() {
        log.info("Reseteando datos de prueba (inscripciones, cursos y secuencias)...");

        inscripcionRepository.deleteAll();
        cursoRepository.deleteAll();
        reiniciarSecuencias();
        poblarCursosIniciales();

        log.info("Datos de prueba reseteados: 3 cursos iniciales, ids desde 1");
    }

    private void reiniciarSecuencias() {
        for (String secuencia : List.of("inscripcion_detalle_seq", "inscripcion_seq", "curso_seq")) {
            try {
                jdbcTemplate.execute("DROP SEQUENCE " + secuencia);
            } catch (Exception ex) {
                log.debug("Secuencia {} no existia o no pudo eliminarse: {}", secuencia, ex.getMessage());
            }
            jdbcTemplate.execute(
                    "CREATE SEQUENCE " + secuencia + " START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE"
            );
        }
    }

    private void poblarCursosIniciales() {
        cursoRepository.save(Curso.builder()
                .nombre("Spring Boot Avanzado")
                .instructor("Prof. Garcia")
                .duracionHoras(40)
                .costo(new BigDecimal("150000"))
                .build());

        cursoRepository.save(Curso.builder()
                .nombre("Docker Cloud Native")
                .instructor("Prof. Ruiz")
                .duracionHoras(30)
                .costo(new BigDecimal("120000"))
                .build());

        cursoRepository.save(Curso.builder()
                .nombre("Oracle Database Cloud")
                .instructor("Prof. Mendez")
                .duracionHoras(35)
                .costo(new BigDecimal("180000"))
                .build());
    }
}
