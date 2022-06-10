ALTER TABLE cuatrimestre
    ALTER anio INT NOT NULL;

ALTER TABLE alumno
    ALTER apellido VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    ALTER carrera VARCHAR(255) NOT NULL;

ALTER TABLE materia
    ALTER carrera VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    ALTER coeficiente DOUBLE NOT NULL;

ALTER TABLE alumno
    ALTER contrasenia VARCHAR(255) NOT NULL;

ALTER TABLE directivo
    ALTER contrasenia VARCHAR(255) NOT NULL;

ALTER TABLE alumno
    ALTER correo VARCHAR(255) NOT NULL;

ALTER TABLE comision
    ALTER cupos_totales INT NOT NULL;

ALTER TABLE horario
    ALTER dia VARCHAR(255) NOT NULL;

ALTER TABLE materia_cursada
    ALTER estado VARCHAR(255) NOT NULL;

ALTER TABLE materia_cursada
    ALTER fecha_de_carga date NOT NULL;

ALTER TABLE horario
    ALTER fin time NOT NULL;

ALTER TABLE cuatrimestre
    ALTER fin_inscripciones datetime NOT NULL;

ALTER TABLE horario
    ALTER inicio time NOT NULL;

ALTER TABLE cuatrimestre
    ALTER inicio_inscripciones datetime NOT NULL;

ALTER TABLE alumno
    ALTER legajo INT NOT NULL;

ALTER TABLE comision
    ALTER modalidad INT NOT NULL;

ALTER TABLE alumno
    ALTER nombre VARCHAR(255) NOT NULL;

ALTER TABLE directivo
    ALTER nombre VARCHAR(255) NOT NULL;

ALTER TABLE materia
    ALTER nombre VARCHAR(255) NOT NULL;

ALTER TABLE comision
    ALTER numero INT NOT NULL;

ALTER TABLE cuatrimestre
    ALTER semestre VARCHAR(255) NOT NULL;

ALTER TABLE comision
    ALTER sobrecupos_ocupados INT NOT NULL;

ALTER TABLE comision
    ALTER sobrecupos_totales INT NOT NULL;