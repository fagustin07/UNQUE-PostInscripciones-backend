CREATE TABLE directivo
(
    correo      VARCHAR(255) NOT NULL,
    nombre      VARCHAR(255) NULL,
    contrasenia VARCHAR(255) NULL,
    rol         VARCHAR(255) NULL,
    CONSTRAINT pk_directivo PRIMARY KEY (correo)
);

ALTER TABLE cuatrimestre
    MODIFY anio INT NULL;

ALTER TABLE alumno
    ADD coeficiente DOUBLE NULL;

ALTER TABLE comision
    MODIFY cupos_totales INT NULL;

ALTER TABLE comision
    MODIFY numero INT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_ocupados INT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_totales INT NULL;