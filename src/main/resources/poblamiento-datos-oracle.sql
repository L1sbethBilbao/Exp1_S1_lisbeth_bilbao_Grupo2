-- Version para Spring Boot init-db (ScriptUtils). Oracle 23+

DROP TABLE IF EXISTS inscripcion_detalles CASCADE CONSTRAINTS;
DROP TABLE IF EXISTS inscripciones CASCADE CONSTRAINTS;
DROP TABLE IF EXISTS cursos CASCADE CONSTRAINTS;

DROP SEQUENCE IF EXISTS inscripcion_detalle_seq;
DROP SEQUENCE IF EXISTS inscripcion_seq;
DROP SEQUENCE IF EXISTS curso_seq;

CREATE SEQUENCE curso_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE cursos (
    id              NUMBER(19)      NOT NULL,
    nombre          VARCHAR2(150)   NOT NULL,
    instructor      VARCHAR2(150)   NOT NULL,
    duracion_horas  NUMBER(10)      NOT NULL,
    costo           NUMBER(12, 2)   NOT NULL,
    CONSTRAINT pk_cursos PRIMARY KEY (id),
    CONSTRAINT uk_cursos_nombre UNIQUE (nombre),
    CONSTRAINT ck_cursos_duracion CHECK (duracion_horas > 0),
    CONSTRAINT ck_cursos_costo CHECK (costo > 0)
);

CREATE SEQUENCE inscripcion_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE inscripciones (
    id                  NUMBER(19)      NOT NULL,
    nombre_estudiante   VARCHAR2(150)   NOT NULL,
    email_estudiante    VARCHAR2(200),
    fecha_inscripcion   TIMESTAMP       NOT NULL,
    total               NUMBER(12, 2)   NOT NULL,
    CONSTRAINT pk_inscripciones PRIMARY KEY (id),
    CONSTRAINT ck_inscripciones_total CHECK (total >= 0)
);

CREATE SEQUENCE inscripcion_detalle_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE inscripcion_detalles (
    id              NUMBER(19)      NOT NULL,
    inscripcion_id  NUMBER(19)      NOT NULL,
    curso_id        NUMBER(19)      NOT NULL,
    costo_unitario  NUMBER(12, 2)   NOT NULL,
    CONSTRAINT pk_inscripcion_detalles PRIMARY KEY (id),
    CONSTRAINT fk_detalle_inscripcion FOREIGN KEY (inscripcion_id) REFERENCES inscripciones (id),
    CONSTRAINT fk_detalle_curso FOREIGN KEY (curso_id) REFERENCES cursos (id),
    CONSTRAINT ck_detalle_costo CHECK (costo_unitario > 0)
);

CREATE INDEX idx_detalle_inscripcion ON inscripcion_detalles (inscripcion_id);
CREATE INDEX idx_detalle_curso ON inscripcion_detalles (curso_id);

INSERT INTO cursos (id, nombre, instructor, duracion_horas, costo)
VALUES (curso_seq.NEXTVAL, 'Spring Boot Avanzado', 'Prof. Garcia', 40, 150000);

INSERT INTO cursos (id, nombre, instructor, duracion_horas, costo)
VALUES (curso_seq.NEXTVAL, 'Docker Cloud Native', 'Prof. Ruiz', 30, 120000);

INSERT INTO cursos (id, nombre, instructor, duracion_horas, costo)
VALUES (curso_seq.NEXTVAL, 'Oracle Database Cloud', 'Prof. Mendez', 35, 180000);
