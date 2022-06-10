ALTER TABLE cuatrimestre
    MODIFY anio INT NOT NULL;

ALTER TABLE alumno
    MODIFY apellido VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    MODIFY carrera VARCHAR(255) NOT NULL;

ALTER TABLE materia
    MODIFY carrera VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    MODIFY coeficiente DOUBLE NOT NULL;

ALTER TABLE alumno
    MODIFY contrasenia VARCHAR(255) NOT NULL;

ALTER TABLE directivo
    MODIFY contrasenia VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    MODIFY correo VARCHAR(255) NOT NULL;

ALTER TABLE comision
    MODIFY cupos_totales INT NOT NULL;

ALTER TABLE horario
    MODIFY dia VARCHAR(255) NOT NULL;

ALTER TABLE materia_cursada
    MODIFY estado VARCHAR(255) NOT NULL;

ALTER TABLE materia_cursada
    MODIFY fecha_de_carga date NOT NULL;

ALTER TABLE horario
    MODIFY fin time NOT NULL;

ALTER TABLE cuatrimestre
    MODIFY fin_inscripciones datetime NOT NULL;

ALTER TABLE horario
    MODIFY inicio time NOT NULL;

ALTER TABLE cuatrimestre
    MODIFY inicio_inscripciones datetime NOT NULL;

ALTER TABLE alumno
    MODIFY legajo INT NOT NULL;

ALTER TABLE comision
    MODIFY modalidad INT NOT NULL;

ALTER TABLE alumno
    MODIFY nombre VARCHAR(255) NOT NULL;

ALTER TABLE directivo
    MODIFY nombre VARCHAR(255) NOT NULL;

ALTER TABLE materia
    MODIFY nombre VARCHAR(255) NOT NULL;

ALTER TABLE comision
    MODIFY numero INT NOT NULL;

ALTER TABLE cuatrimestre
    MODIFY semestre VARCHAR(255) NOT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_ocupados INT NOT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_totales INT NOT NULL;