ALTER TABLE cuatrimestre
    MODIFY anio INT NULL;

ALTER TABLE alumno
    MODIFY coeficiente DOUBLE NULL;

ALTER TABLE comision
    MODIFY cupos_totales INT NULL;

ALTER TABLE comision
    MODIFY numero INT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_ocupados INT NULL;

ALTER TABLE comision
    MODIFY sobrecupos_totales INT NULL;