CREATE TABLE directivo
(
    correo      VARCHAR(255) NOT NULL,
    nombre      VARCHAR(255) NULL,
    contrasenia VARCHAR(255) NULL,
    rol         VARCHAR(255) NULL,
    CONSTRAINT pk_directivo PRIMARY KEY (correo)
);

ALTER TABLE cuatrimestre
    ALTER anio INT NULL;

ALTER TABLE alumno
    ADD coeficiente DOUBLE NULL;

ALTER TABLE comision
    ALTER cupos_totales INT NULL;

ALTER TABLE comision
    ALTER numero INT NULL;

ALTER TABLE comision
    ALTER sobrecupos_ocupados INT NULL;

ALTER TABLE comision
    ALTER sobrecupos_totales INT NULL;