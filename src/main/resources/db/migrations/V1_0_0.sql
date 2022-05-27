CREATE TABLE alumno
(
    dni         INT          NOT NULL,
    nombre      VARCHAR(255) NULL,
    apellido    VARCHAR(255) NULL,
    correo      VARCHAR(255) NULL,
    legajo      INT          NULL,
    contrasenia VARCHAR(255) NULL,
    carrera     VARCHAR(255) NULL,
    CONSTRAINT pk_alumno PRIMARY KEY (dni)
);

CREATE TABLE alumno_formularios
(
    alumno_dni     INT    NOT NULL,
    formularios_id BIGINT NOT NULL
);

CREATE TABLE alumno_historia_academica
(
    alumno_dni            INT    NOT NULL,
    historia_academica_id BIGINT NOT NULL
);

CREATE TABLE comision
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    materia_codigo      VARCHAR(255)          NULL,
    numero              INT                   NULL,
    cuatrimestre_id     BIGINT                NULL,
    cupos_totales       INT                   NULL,
    sobrecupos_totales  INT                   NULL,
    modalidad           INT                   NULL,
    sobrecupos_ocupados INT                   NULL,
    CONSTRAINT pk_comision PRIMARY KEY (id)
);

CREATE TABLE comision_horarios
(
    comision_id BIGINT NOT NULL,
    horarios_id BIGINT NOT NULL
);

CREATE TABLE cuatrimestre
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    anio                 INT                   NULL,
    semestre             VARCHAR(255)          NULL,
    inicio_inscripciones datetime              NULL,
    fin_inscripciones    datetime              NULL,
    CONSTRAINT pk_cuatrimestre PRIMARY KEY (id)
);

CREATE TABLE formulario
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    cuatrimestre_id BIGINT                NULL,
    estado          VARCHAR(255)          NULL,
    CONSTRAINT pk_formulario PRIMARY KEY (id)
);

CREATE TABLE formulario_solicitudes
(
    formulario_id  BIGINT NOT NULL,
    solicitudes_id BIGINT NOT NULL
);

CREATE TABLE horario
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    dia    VARCHAR(255)          NULL,
    inicio time                  NULL,
    fin    time                  NULL,
    CONSTRAINT pk_horario PRIMARY KEY (id)
);

CREATE TABLE materia
(
    codigo  VARCHAR(255) NOT NULL,
    nombre  VARCHAR(255) NULL,
    carrera VARCHAR(255) NULL,
    CONSTRAINT pk_materia PRIMARY KEY (codigo)
);

CREATE TABLE materia_correlativas
(
    materia_codigo      VARCHAR(255) NOT NULL,
    correlativas_codigo VARCHAR(255) NOT NULL
);

CREATE TABLE materia_cursada
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    materia_codigo VARCHAR(255)          NULL,
    fecha_de_carga date                  NULL,
    estado         VARCHAR(255)          NULL,
    CONSTRAINT pk_materiacursada PRIMARY KEY (id)
);

CREATE TABLE solicitud_sobrecupo
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    comision_id BIGINT                NULL,
    estado      VARCHAR(255)          NULL,
    CONSTRAINT pk_solicitudsobrecupo PRIMARY KEY (id)
);

ALTER TABLE alumno_formularios
    ADD CONSTRAINT uc_alumno_formularios_formularios UNIQUE (formularios_id);

ALTER TABLE alumno_historia_academica
    ADD CONSTRAINT uc_alumno_historia_academica_historiaacademica UNIQUE (historia_academica_id);

ALTER TABLE alumno
    ADD CONSTRAINT uc_alumno_legajo UNIQUE (legajo);

ALTER TABLE comision_horarios
    ADD CONSTRAINT uc_comision_horarios_horarios UNIQUE (horarios_id);

ALTER TABLE formulario_solicitudes
    ADD CONSTRAINT uc_formulario_solicitudes_solicitudes UNIQUE (solicitudes_id);

ALTER TABLE materia
    ADD CONSTRAINT uc_materia_nombre UNIQUE (nombre);

ALTER TABLE cuatrimestre
    ADD CONSTRAINT unique_anio_semestre UNIQUE (anio, semestre);

ALTER TABLE comision
    ADD CONSTRAINT FK_COMISION_ON_CUATRIMESTRE FOREIGN KEY (cuatrimestre_id) REFERENCES cuatrimestre (id);

ALTER TABLE comision
    ADD CONSTRAINT FK_COMISION_ON_MATERIA_CODIGO FOREIGN KEY (materia_codigo) REFERENCES materia (codigo);

ALTER TABLE formulario
    ADD CONSTRAINT FK_FORMULARIO_ON_CUATRIMESTRE FOREIGN KEY (cuatrimestre_id) REFERENCES cuatrimestre (id);

ALTER TABLE materia_cursada
    ADD CONSTRAINT FK_MATERIACURSADA_ON_MATERIA_CODIGO FOREIGN KEY (materia_codigo) REFERENCES materia (codigo);

ALTER TABLE solicitud_sobrecupo
    ADD CONSTRAINT FK_SOLICITUDSOBRECUPO_ON_COMISION FOREIGN KEY (comision_id) REFERENCES comision (id);

ALTER TABLE alumno_formularios
    ADD CONSTRAINT fk_alufor_on_alumno FOREIGN KEY (alumno_dni) REFERENCES alumno (dni);

ALTER TABLE alumno_formularios
    ADD CONSTRAINT fk_alufor_on_formulario FOREIGN KEY (formularios_id) REFERENCES formulario (id);

ALTER TABLE alumno_historia_academica
    ADD CONSTRAINT fk_aluhisaca_on_alumno FOREIGN KEY (alumno_dni) REFERENCES alumno (dni);

ALTER TABLE alumno_historia_academica
    ADD CONSTRAINT fk_aluhisaca_on_materia_cursada FOREIGN KEY (historia_academica_id) REFERENCES materia_cursada (id);

ALTER TABLE comision_horarios
    ADD CONSTRAINT fk_comhor_on_comision FOREIGN KEY (comision_id) REFERENCES comision (id);

ALTER TABLE comision_horarios
    ADD CONSTRAINT fk_comhor_on_horario FOREIGN KEY (horarios_id) REFERENCES horario (id);

ALTER TABLE formulario_solicitudes
    ADD CONSTRAINT fk_forsol_on_formulario FOREIGN KEY (formulario_id) REFERENCES formulario (id);

ALTER TABLE formulario_solicitudes
    ADD CONSTRAINT fk_forsol_on_solicitud_sobrecupo FOREIGN KEY (solicitudes_id) REFERENCES solicitud_sobrecupo (id);

ALTER TABLE materia_correlativas
    ADD CONSTRAINT fk_matcor_on_correlativas_codigo FOREIGN KEY (correlativas_codigo) REFERENCES materia (codigo);

ALTER TABLE materia_correlativas
    ADD CONSTRAINT fk_matcor_on_materia_codigo FOREIGN KEY (materia_codigo) REFERENCES materia (codigo);